package com.smart.web.controller.campus;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.utils.StringUtils;
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

    @PostMapping("/session/update")
    public AjaxResult updateSession(@Validated @RequestBody ScQaSession scQaSession)
    {
        return toAjax(scQaSessionService.updateScQaSession(scQaSession));
    }

    @PostMapping("/session/delete/{sessionId}")
    public AjaxResult deleteSession(@PathVariable Long sessionId)
    {
        ScQaMessage query = new ScQaMessage();
        query.setSessionId(sessionId);
        List<ScQaMessage> messages = scQaMessageService.selectScQaMessageList(query);
        if (messages != null && !messages.isEmpty())
        {
            Long[] messageIds = messages.stream().map(ScQaMessage::getMessageId).toArray(Long[]::new);
            scQaMessageService.deleteScQaMessageByMessageIds(messageIds);
        }
        return toAjax(scQaSessionService.deleteScQaSessionBySessionId(sessionId));
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

    @GetMapping("/model/options")
    public AjaxResult modelOptions()
    {
        ScAiModelConfig query = new ScAiModelConfig();
        query.setStatus("0");
        query.setModelType("chat");
        List<ScAiModelConfig> list = scAiModelConfigService.selectScAiModelConfigList(query);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (ScAiModelConfig item : list)
        {
            Map<String, Object> row = new HashMap<>();
            row.put("modelId", item.getModelId());
            row.put("modelName", item.getModelName());
            row.put("provider", item.getProvider());
            row.put("isDefault", item.getIsDefault());
            row.put("priority", item.getPriority());
            row.put("bizTypes", item.getBizTypes());
            row.put("supportStream", item.getSupportStream());
            row.put("supportReasoning", item.getSupportReasoning());
            row.put("supportVision", item.getSupportVision());
            result.add(row);
        }
        return success(result);
    }

    @PostMapping("/ask")
    public AjaxResult ask(@RequestBody QaAskDto askDto)
    {
        try
        {
            AskContext context = prepareAskContext(askDto, false);
            AiChatResponseVo aiResponse = aiGatewayService.chat(context.requestDto);
            ScQaMessage assistantMessage = saveAssistantMessage(context.session, aiResponse, context.operatorName);

            AjaxResult ajax = AjaxResult.success();
            ajax.put("sessionId", context.session.getSessionId());
            ajax.put("userMessage", context.userMessage);
            ajax.put("assistantMessage", assistantMessage);
            ajax.put("aiResponse", aiResponse);
            return ajax;
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    @PostMapping("/ask/stream")
    public SseEmitter askStream(@RequestBody QaAskDto askDto)
    {
        SseEmitter emitter = new SseEmitter(0L);
        final AskContext context;
        try
        {
            context = prepareAskContext(askDto, true);
        }
        catch (Exception e)
        {
            try
            {
                sendEvent(emitter, "error", mapOf("message", e.getMessage()));
            }
            catch (IOException ignored)
            {
            }
            emitter.complete();
            return emitter;
        }

        CompletableFuture.runAsync(() -> {
            try
            {
                sendEvent(emitter, "session", mapOf("sessionId", context.session.getSessionId(), "modelId", context.requestDto.getModelId()));
                sendEvent(emitter, "start", mapOf("message", "开始生成回答"));
                StringBuilder contentBuilder = new StringBuilder();
                AiChatResponseVo aiResponse = aiGatewayService.streamChat(context.requestDto, chunk -> {
                    try
                    {
                        contentBuilder.append(chunk);
                        sendEvent(emitter, "chunk", mapOf("content", chunk));
                    }
                    catch (IOException ioException)
                    {
                        throw new RuntimeException(ioException);
                    }
                });
                aiResponse.setContent(contentBuilder.toString());
                ScQaMessage assistantMessage = saveAssistantMessage(context.session, aiResponse, context.operatorName);
                Map<String, Object> donePayload = new HashMap<>();
                donePayload.put("sessionId", context.session.getSessionId());
                donePayload.put("assistantMessage", assistantMessage);
                donePayload.put("aiResponse", aiResponse);
                sendEvent(emitter, "done", donePayload);
            }
            catch (Exception e)
            {
                try
                {
                    sendEvent(emitter, "error", mapOf("message", e.getMessage()));
                }
                catch (IOException ignored)
                {
                }
            }
            finally
            {
                emitter.complete();
            }
        });
        return emitter;
    }

    private AskContext prepareAskContext(QaAskDto askDto, boolean stream)
    {
        if (askDto == null || askDto.getUserId() == null || StringUtils.isEmpty(askDto.getQuestion()))
        {
            throw new IllegalArgumentException("提问参数不能为空");
        }
        String operatorName = getUsername();
        ScQaSession session = createOrLoadSession(askDto, operatorName);
        ScQaMessage userMessage = createUserMessage(session.getSessionId(), askDto, operatorName);
        Long modelId = scAiModelConfigService.resolveModel("chat", "qa", askDto.getModelId()).getModelId();
        String systemPrompt = resolveSystemPrompt(askDto);
        AiChatRequestDto requestDto = new AiChatRequestDto();
        requestDto.setModelId(modelId);
        requestDto.setBizType("qa");
        requestDto.setSystemPrompt(systemPrompt);
        requestDto.setUserPrompt(askDto.getQuestion());
        requestDto.setDeepThinking(Boolean.TRUE.equals(askDto.getDeepThinking()));
        requestDto.setStream(stream);
        requestDto.setImages(askDto.getImages());
        AskContext context = new AskContext();
        context.session = session;
        context.userMessage = userMessage;
        context.requestDto = requestDto;
        context.operatorName = operatorName;
        return context;
    }

    private ScQaSession createOrLoadSession(QaAskDto askDto, String operatorName)
    {
        ScQaSession session;
        if (askDto.getSessionId() == null)
        {
            session = new ScQaSession();
            session.setUserId(askDto.getUserId());
            session.setCourseId(askDto.getCourseId());
            session.setSessionTitle(StringUtils.isEmpty(askDto.getSessionTitle()) ? "新问答会话" : askDto.getSessionTitle());
            session.setSourceType("course");
            session.setStatus("0");
            session.setCreateBy(operatorName);
            scQaSessionService.insertScQaSession(session);
        }
        else
        {
            session = scQaSessionService.selectScQaSessionBySessionId(askDto.getSessionId());
            if (session == null)
            {
                throw new IllegalArgumentException("问答会话不存在");
            }
        }
        return session;
    }

    private ScQaMessage createUserMessage(Long sessionId, QaAskDto askDto, String operatorName)
    {
        ScQaMessage userMessage = new ScQaMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setRoleType("user");
        userMessage.setContent(askDto.getQuestion());
        userMessage.setReferenceSource(buildAttachmentSource(askDto));
        userMessage.setTokenCount(0);
        userMessage.setLatencyMs(0);
        userMessage.setSensitiveFlag("0");
        userMessage.setCreateBy(operatorName);
        scQaMessageService.insertScQaMessage(userMessage);
        return userMessage;
    }

    private String buildAttachmentSource(QaAskDto askDto)
    {
        if (askDto.getImages() == null || askDto.getImages().isEmpty())
        {
            return "";
        }
        java.util.List<String> names = new java.util.ArrayList<>();
        askDto.getImages().forEach(item -> names.add(item.getName()));
        return "附件：" + String.join(", ", names);
    }

    private String resolveSystemPrompt(QaAskDto askDto)
    {
        String systemPrompt = "你是智慧校园自主学习助手，请围绕课程学习、资源推荐、知识点理解与考试答疑做出简洁、专业的回答。";
        ScAiPromptTemplate promptQuery = new ScAiPromptTemplate();
        promptQuery.setBizType("qa");
        promptQuery.setStatus("0");
        List<ScAiPromptTemplate> promptList = scAiPromptTemplateService.selectScAiPromptTemplateList(promptQuery);
        if (!promptList.isEmpty() && StringUtils.isNotEmpty(promptList.get(0).getPromptContent()))
        {
            systemPrompt = promptList.get(0).getPromptContent();
        }
        if (Boolean.TRUE.equals(askDto.getDeepThinking()))
        {
            systemPrompt += "\n请先给出结构化分析，再给出明确结论与建议，必要时分点输出。";
        }
        if (askDto.getImages() != null && !askDto.getImages().isEmpty())
        {
            systemPrompt += "\n如果用户上传了图片，请结合图片内容回答；若当前模型不支持视觉能力，请明确说明。";
        }
        return systemPrompt;
    }

    private ScQaMessage saveAssistantMessage(ScQaSession session, AiChatResponseVo aiResponse, String operatorName)
    {
        ScQaMessage assistantMessage = new ScQaMessage();
        assistantMessage.setSessionId(session.getSessionId());
        assistantMessage.setRoleType("assistant");
        assistantMessage.setContent(aiResponse.getContent());
        assistantMessage.setReferenceSource("AI模型：" + aiResponse.getModelName());
        assistantMessage.setTokenCount(aiResponse.getTokenUsed());
        assistantMessage.setLatencyMs(aiResponse.getDurationMs());
        assistantMessage.setSensitiveFlag("0");
        assistantMessage.setCreateBy(operatorName);
        scQaMessageService.insertScQaMessage(assistantMessage);
        return assistantMessage;
    }

    private void sendEvent(SseEmitter emitter, String eventName, Object payload) throws IOException
    {
        emitter.send(SseEmitter.event().name(eventName).data(payload));
    }

    private Map<String, Object> mapOf(Object... keyValues)
    {
        Map<String, Object> map = new HashMap<>();
        if (keyValues == null)
        {
            return map;
        }
        for (int index = 0; index + 1 < keyValues.length; index += 2)
        {
            Object key = keyValues[index];
            Object value = keyValues[index + 1];
            if (key != null)
            {
                map.put(String.valueOf(key), value);
            }
        }
        return map;
    }

    private static class AskContext
    {
        private ScQaSession session;
        private ScQaMessage userMessage;
        private AiChatRequestDto requestDto;
        private String operatorName;
    }
}
