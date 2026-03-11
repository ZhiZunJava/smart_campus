package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.system.domain.ScAiModelConfig;
import com.smart.system.domain.ScAiPromptTemplate;
import com.smart.system.domain.ScQaFeedback;
import com.smart.system.domain.ScQaMessage;
import com.smart.system.domain.ScQaSession;
import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.dto.AiChatRequestDto;
import com.smart.system.domain.dto.QaAskDto;
import com.smart.system.service.IScAiModelConfigService;
import com.smart.system.service.IScAiPromptTemplateService;
import com.smart.system.service.IScQaFeedbackService;
import com.smart.system.service.IScQaMessageService;
import com.smart.system.service.IScQaSessionService;
import com.smart.system.service.ai.IAiGatewayService;

@RestController
@RequestMapping("/campus/qa")
public class ScQaController extends BaseController
{
    @Autowired
    private IScQaSessionService scQaSessionService;
    @Autowired
    private IScQaMessageService scQaMessageService;
    @Autowired
    private IScQaFeedbackService scQaFeedbackService;
    @Autowired
    private IScAiModelConfigService scAiModelConfigService;
    @Autowired
    private IScAiPromptTemplateService scAiPromptTemplateService;
    @Autowired
    private IAiGatewayService aiGatewayService;

    @GetMapping("/session/list")
    public TableDataInfo sessionList(ScQaSession scQaSession)
    {
        startPage();
        List<ScQaSession> list = scQaSessionService.selectScQaSessionList(scQaSession);
        return getDataTable(list);
    }

    @PostMapping("/session")
    public AjaxResult addSession(@Validated @RequestBody ScQaSession scQaSession)
    {
        scQaSession.setCreateBy(getUsername());
        return toAjax(scQaSessionService.insertScQaSession(scQaSession));
    }

    @GetMapping("/message/list")
    public TableDataInfo messageList(ScQaMessage scQaMessage)
    {
        startPage();
        List<ScQaMessage> list = scQaMessageService.selectScQaMessageList(scQaMessage);
        return getDataTable(list);
    }

    @PostMapping("/message")
    public AjaxResult addMessage(@Validated @RequestBody ScQaMessage scQaMessage)
    {
        scQaMessage.setCreateBy(getUsername());
        return toAjax(scQaMessageService.insertScQaMessage(scQaMessage));
    }

    @GetMapping("/feedback/list")
    public TableDataInfo feedbackList(ScQaFeedback scQaFeedback)
    {
        startPage();
        List<ScQaFeedback> list = scQaFeedbackService.selectScQaFeedbackList(scQaFeedback);
        return getDataTable(list);
    }

    @PostMapping("/feedback")
    public AjaxResult addFeedback(@Validated @RequestBody ScQaFeedback scQaFeedback)
    {
        scQaFeedback.setCreateBy(getUsername());
        return toAjax(scQaFeedbackService.insertScQaFeedback(scQaFeedback));
    }

    @PostMapping("/ask")
    public AjaxResult ask(@RequestBody QaAskDto askDto)
    {
        if (askDto == null || askDto.getUserId() == null || askDto.getQuestion() == null || askDto.getQuestion().trim().isEmpty())
        {
            return error("提问参数不能为空");
        }

        ScQaSession session;
        if (askDto.getSessionId() == null)
        {
            session = new ScQaSession();
            session.setUserId(askDto.getUserId());
            session.setCourseId(askDto.getCourseId());
            session.setSessionTitle((askDto.getSessionTitle() == null || askDto.getSessionTitle().isEmpty()) ? "新问答会话" : askDto.getSessionTitle());
            session.setSourceType("course");
            session.setStatus("0");
            session.setCreateBy(getUsername());
            scQaSessionService.insertScQaSession(session);
        }
        else
        {
            session = scQaSessionService.selectScQaSessionBySessionId(askDto.getSessionId());
            if (session == null)
            {
                return error("问答会话不存在");
            }
        }

        ScQaMessage userMessage = new ScQaMessage();
        userMessage.setSessionId(session.getSessionId());
        userMessage.setRoleType("user");
        userMessage.setContent(askDto.getQuestion());
        userMessage.setReferenceSource("");
        userMessage.setTokenCount(0);
        userMessage.setLatencyMs(0);
        userMessage.setSensitiveFlag("0");
        userMessage.setCreateBy(getUsername());
        scQaMessageService.insertScQaMessage(userMessage);

        Long modelId = askDto.getModelId();
        if (modelId == null)
        {
            ScAiModelConfig query = new ScAiModelConfig();
            query.setStatus("0");
            query.setModelType("chat");
            java.util.List<ScAiModelConfig> modelList = scAiModelConfigService.selectScAiModelConfigList(query);
            if (modelList.isEmpty())
            {
                return error("未找到可用的聊天模型，请先在AI管理中配置模型");
            }
            modelId = modelList.get(0).getModelId();
        }

        String systemPrompt = "你是智慧校园自主学习助手，请围绕课程学习、资源推荐、知识点理解与考试答疑做出简洁、专业的回答。";
        ScAiPromptTemplate promptQuery = new ScAiPromptTemplate();
        promptQuery.setBizType("qa");
        promptQuery.setStatus("0");
        java.util.List<ScAiPromptTemplate> promptList = scAiPromptTemplateService.selectScAiPromptTemplateList(promptQuery);
        if (!promptList.isEmpty() && promptList.get(0).getPromptContent() != null && !promptList.get(0).getPromptContent().isEmpty())
        {
            systemPrompt = promptList.get(0).getPromptContent();
        }

        AiChatRequestDto requestDto = new AiChatRequestDto();
        requestDto.setModelId(modelId);
        requestDto.setSystemPrompt(systemPrompt);
        requestDto.setUserPrompt(askDto.getQuestion());
        AiChatResponseVo aiResponse = aiGatewayService.chat(requestDto);

        ScQaMessage assistantMessage = new ScQaMessage();
        assistantMessage.setSessionId(session.getSessionId());
        assistantMessage.setRoleType("assistant");
        assistantMessage.setContent(aiResponse.getContent());
        assistantMessage.setReferenceSource("AI模型：" + aiResponse.getModelName());
        assistantMessage.setTokenCount(aiResponse.getTokenUsed());
        assistantMessage.setLatencyMs(aiResponse.getDurationMs());
        assistantMessage.setSensitiveFlag("0");
        assistantMessage.setCreateBy(getUsername());
        scQaMessageService.insertScQaMessage(assistantMessage);

        AjaxResult ajax = AjaxResult.success();
        ajax.put("sessionId", session.getSessionId());
        ajax.put("userMessage", userMessage);
        ajax.put("assistantMessage", assistantMessage);
        ajax.put("aiResponse", aiResponse);
        return ajax;
    }
}
