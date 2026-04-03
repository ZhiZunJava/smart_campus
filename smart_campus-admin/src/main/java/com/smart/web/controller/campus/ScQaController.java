package com.smart.web.controller.campus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.smart.common.config.RuoYiConfig;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.utils.StringUtils;
import com.smart.common.utils.file.FileUploadUtils;
import com.smart.common.utils.file.FileUtils;
import com.smart.system.domain.ScAiModelConfig;
import com.smart.system.domain.ScAiPromptTemplate;
import com.smart.system.domain.ScQaAttachment;
import com.smart.system.domain.ScQaFeedback;
import com.smart.system.domain.ScQaMessage;
import com.smart.system.domain.ScQaSession;
import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.dto.AiChatRequestDto;
import com.smart.system.domain.dto.AiHistoryMessageDto;
import com.smart.system.domain.dto.QaAskDto;
import com.smart.system.service.IScAiModelConfigService;
import com.smart.system.service.IScAiPromptTemplateService;
import com.smart.system.service.IScQaAttachmentService;
import com.smart.system.service.IScQaFeedbackService;
import com.smart.system.service.IScQaMessageService;
import com.smart.system.service.IScQaSessionService;
import com.smart.system.service.ai.IAiGatewayService;
import com.smart.framework.config.ServerConfig;

@RestController
@RequestMapping("/campus/qa")
public class ScQaController extends BaseController {
    private static final Pattern CODE_FENCE_PATTERN = Pattern.compile("(```[\\s\\S]*?```|~~~[\\s\\S]*?~~~)");
    private static final Pattern SIMPLE_GREETING_PATTERN = Pattern
            .compile("^(你好|您好|嗨|哈喽|hello|hi|hey|在吗|测试|test|早上好|下午好|晚上好)[!！,.，。?？~～ ]*$",
                    Pattern.CASE_INSENSITIVE);
    private static final int QA_HISTORY_MESSAGE_LIMIT = 8;
    private static final int QA_HISTORY_CONTENT_MAX_LENGTH = 1200;

    @Autowired
    private IScQaSessionService scQaSessionService;
    @Autowired
    private IScQaAttachmentService scQaAttachmentService;
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
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ServerConfig serverConfig;

    @GetMapping("/session/list")
    public TableDataInfo sessionList(ScQaSession scQaSession) {
        startPage();
        List<ScQaSession> list = scQaSessionService.selectScQaSessionList(scQaSession);
        return getDataTable(list);
    }

    @PostMapping("/session")
    public AjaxResult addSession(@Validated @RequestBody ScQaSession scQaSession) {
        scQaSession.setCreateBy(getUsername());
        return toAjax(scQaSessionService.insertScQaSession(scQaSession));
    }

    @PostMapping("/session/update")
    public AjaxResult updateSession(@Validated @RequestBody ScQaSession scQaSession) {
        return toAjax(scQaSessionService.updateScQaSession(scQaSession));
    }

    @PostMapping("/session/delete/{sessionId}")
    public AjaxResult deleteSession(@PathVariable Long sessionId) {
        ScQaMessage query = new ScQaMessage();
        query.setSessionId(sessionId);
        List<ScQaMessage> messages = scQaMessageService.selectScQaMessageList(query);
        if (messages != null && !messages.isEmpty()) {
            Long[] messageIds = messages.stream().map(ScQaMessage::getMessageId).toArray(Long[]::new);
            scQaMessageService.deleteScQaMessageByMessageIds(messageIds);
        }
        cleanupSessionAttachments(sessionId);
        return toAjax(scQaSessionService.deleteScQaSessionBySessionId(sessionId));
    }

    @GetMapping("/message/list")
    public TableDataInfo messageList(ScQaMessage scQaMessage) {
        startPage();
        List<ScQaMessage> list = scQaMessageService.selectScQaMessageList(scQaMessage);
        enrichMessageFeedback(list, getUserId());
        return getDataTable(list);
    }

    @PostMapping("/message")
    public AjaxResult addMessage(@Validated @RequestBody ScQaMessage scQaMessage) {
        scQaMessage.setCreateBy(getUsername());
        return toAjax(scQaMessageService.insertScQaMessage(scQaMessage));
    }

    @GetMapping("/feedback/list")
    public TableDataInfo feedbackList(ScQaFeedback scQaFeedback) {
        startPage();
        List<ScQaFeedback> list = scQaFeedbackService.selectScQaFeedbackList(scQaFeedback);
        return getDataTable(list);
    }

    @PostMapping("/feedback")
    public AjaxResult addFeedback(@Validated @RequestBody ScQaFeedback scQaFeedback) {
        if (scQaFeedback == null || scQaFeedback.getMessageId() == null) {
            return error("反馈消息不能为空");
        }
        Long userId = getUserId();
        if (userId == null) {
            return error("当前未获取到登录用户");
        }
        String feedbackType = StringUtils.lowerCase(StringUtils.trimToEmpty(scQaFeedback.getFeedbackType()));
        if (!"helpful".equals(feedbackType) && !"unhelpful".equals(feedbackType)) {
            return error("反馈类型不合法");
        }
        scQaFeedback.setUserId(userId);
        scQaFeedback.setFeedbackType(feedbackType);

        ScQaFeedback query = new ScQaFeedback();
        query.setMessageId(scQaFeedback.getMessageId());
        query.setUserId(userId);
        List<ScQaFeedback> existingList = scQaFeedbackService.selectScQaFeedbackList(query);
        if (existingList != null && !existingList.isEmpty()) {
            ScQaFeedback existing = existingList.get(0);
            existing.setFeedbackType(feedbackType);
            existing.setFeedbackContent(scQaFeedback.getFeedbackContent());
            existing.setUpdateBy(getUsername());
            return toAjax(scQaFeedbackService.updateScQaFeedback(existing));
        }

        scQaFeedback.setCreateBy(getUsername());
        return toAjax(scQaFeedbackService.insertScQaFeedback(scQaFeedback));
    }

    private void enrichMessageFeedback(List<ScQaMessage> messages, Long userId) {
        if (messages == null || messages.isEmpty() || userId == null) {
            return;
        }
        Set<Long> messageIds = messages.stream()
                .map(ScQaMessage::getMessageId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (messageIds.isEmpty()) {
            return;
        }
        ScQaFeedback query = new ScQaFeedback();
        query.setUserId(userId);
        Map<Long, ScQaFeedback> feedbackMap = scQaFeedbackService.selectScQaFeedbackList(query).stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getMessageId() != null && messageIds.contains(item.getMessageId()))
                .collect(Collectors.toMap(
                        ScQaFeedback::getMessageId,
                        item -> item,
                        (left, right) -> left,
                        LinkedHashMap::new));
        for (ScQaMessage message : messages) {
            if (message == null || message.getMessageId() == null) {
                continue;
            }
            ScQaFeedback feedback = feedbackMap.get(message.getMessageId());
            if (feedback == null) {
                continue;
            }
            message.setFeedbackType(feedback.getFeedbackType());
            message.setFeedbackContent(feedback.getFeedbackContent());
        }
    }

    @PostMapping("/attachment/upload")
    public AjaxResult uploadAttachment(MultipartFile file, Long sessionId) throws Exception {
        if (file == null || file.isEmpty()) {
            return error("上传文件不能为空");
        }
        String filePath = RuoYiConfig.getUploadPath() + "/qa";
        String fileName = FileUploadUtils.upload(filePath, file);
        String url = serverConfig.getUrl() + fileName;

        ScQaAttachment attachment = new ScQaAttachment();
        attachment.setSessionId(sessionId);
        attachment.setUserId(getUserId());
        attachment.setFileName(fileName);
        attachment.setOriginalName(file.getOriginalFilename());
        attachment.setFileUrl(url);
        attachment.setMimeType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setStorageType("local");
        attachment.setStatus("0");
        attachment.setCreateBy(getUsername());
        scQaAttachmentService.insertScQaAttachment(attachment);

        AjaxResult ajax = AjaxResult.success();
        ajax.put("attachmentId", attachment.getAttachmentId());
        ajax.put("url", url);
        ajax.put("fileName", fileName);
        ajax.put("originalFilename", file.getOriginalFilename());
        ajax.put("mimeType", file.getContentType());
        ajax.put("size", file.getSize());
        return ajax;
    }

    @PostMapping("/attachment/delete/{attachmentId}")
    public AjaxResult deleteAttachment(@PathVariable Long attachmentId) {
        ScQaAttachment attachment = scQaAttachmentService.selectScQaAttachmentByAttachmentId(attachmentId);
        if (attachment == null) {
            return error("附件不存在");
        }
        if (attachment.getUserId() != null && !attachment.getUserId().equals(getUserId())) {
            return error("无权删除该附件");
        }
        deleteAttachmentFile(attachment);
        return toAjax(scQaAttachmentService.deleteScQaAttachmentByAttachmentId(attachmentId));
    }

    @GetMapping("/model/options")
    public AjaxResult modelOptions() {
        ScAiModelConfig query = new ScAiModelConfig();
        query.setStatus("0");
        query.setModelType("chat");
        List<ScAiModelConfig> list = scAiModelConfigService.selectScAiModelConfigList(query);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (ScAiModelConfig item : list) {
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
    public AjaxResult ask(@RequestBody QaAskDto askDto) {
        try {
            AskContext context = prepareAskContext(askDto, false);
            AiChatResponseVo aiResponse = finalizeAssistantResponse(
                    aiGatewayService.chat(context.requestDto),
                    context.requestDto.getSystemPrompt(),
                    context.deepThinkingApplied);
            ScQaMessage assistantMessage = saveAssistantMessage(context.session, aiResponse, context.operatorName);

            AjaxResult ajax = AjaxResult.success();
            ajax.put("sessionId", context.session.getSessionId());
            ajax.put("userMessage", context.userMessage);
            ajax.put("assistantMessage", assistantMessage);
            ajax.put("aiResponse", aiResponse);
            return ajax;
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PostMapping("/ask/stream")
    public SseEmitter askStream(@RequestBody QaAskDto askDto) {
        SseEmitter emitter = new SseEmitter(0L);
        final AskContext context;
        try {
            context = prepareAskContext(askDto, true);
        } catch (Exception e) {
            try {
                sendEvent(emitter, "error", mapOf("message", e.getMessage()));
            } catch (IOException ignored) {
            }
            emitter.complete();
            return emitter;
        }

        CompletableFuture.runAsync(() -> {
            try {
                sendEvent(emitter, "session",
                        mapOf("sessionId", context.session.getSessionId(), "modelId", context.requestDto.getModelId()));
                sendEvent(emitter, "start", mapOf("message", "开始生成回答"));
                StringBuilder contentBuilder = new StringBuilder();
                ReasoningStreamState reasoningState = new ReasoningStreamState();
                AiChatResponseVo aiResponse = aiGatewayService.streamChat(context.requestDto, chunk -> {
                    try {
                        if ("reasoning".equals(chunk.getType())) {
                            String reasoningDelta = reasoningState.appendAndCollectVisible(
                                    StringUtils.defaultString(chunk.getContent()),
                                    context.requestDto.getSystemPrompt());
                            if (StringUtils.isNotEmpty(reasoningDelta)) {
                                sendEvent(emitter, "reasoning", mapOf("content", reasoningDelta));
                            }
                            return;
                        }
                        contentBuilder.append(chunk.getContent());
                        sendEvent(emitter, "chunk", mapOf("content", chunk.getContent()));
                    } catch (IOException ioException) {
                        throw new RuntimeException(ioException);
                    }
                });
                String remainingReasoningDelta = reasoningState.flushRemaining(context.requestDto.getSystemPrompt());
                if (StringUtils.isNotEmpty(remainingReasoningDelta)) {
                    sendEvent(emitter, "reasoning", mapOf("content", remainingReasoningDelta));
                }
                aiResponse.setContent(contentBuilder.toString());
                aiResponse = finalizeAssistantResponse(
                        aiResponse,
                        context.requestDto.getSystemPrompt(),
                        context.deepThinkingApplied,
                        reasoningState.getVisibleReasoning());
                ScQaMessage assistantMessage = saveAssistantMessage(context.session, aiResponse, context.operatorName);
                Map<String, Object> donePayload = new HashMap<>();
                donePayload.put("sessionId", context.session.getSessionId());
                donePayload.put("assistantMessage", assistantMessage);
                donePayload.put("aiResponse", aiResponse);
                sendEvent(emitter, "done", donePayload);
            } catch (Exception e) {
                try {
                    sendEvent(emitter, "error", mapOf("message", e.getMessage()));
                } catch (IOException ignored) {
                }
            } finally {
                emitter.complete();
            }
        });
        return emitter;
    }

    private AskContext prepareAskContext(QaAskDto askDto, boolean stream) {
        if (askDto == null || askDto.getUserId() == null || StringUtils.isEmpty(askDto.getQuestion())) {
            throw new IllegalArgumentException("提问参数不能为空");
        }
        String operatorName = getUsername();
        ScQaSession session = createOrLoadSession(askDto, operatorName);
        List<AiHistoryMessageDto> historyMessages = loadRecentHistoryMessages(session.getSessionId(),
                QA_HISTORY_MESSAGE_LIMIT);
        ScQaMessage userMessage = createUserMessage(session.getSessionId(), askDto, operatorName);
        Long modelId = scAiModelConfigService.resolveModel("chat", "qa", askDto.getModelId()).getModelId();
        String systemPrompt = resolveSystemPrompt(askDto);
        boolean deepThinkingApplied = shouldEnableDeepThinking(askDto);
        AiChatRequestDto requestDto = new AiChatRequestDto();
        requestDto.setModelId(modelId);
        requestDto.setBizType("qa");
        requestDto.setSystemPrompt(systemPrompt);
        requestDto.setUserPrompt(buildQaUserPrompt(askDto));
        requestDto.setDeepThinking(deepThinkingApplied);
        requestDto.setStream(stream);
        requestDto.setImages(askDto.getImages());
        requestDto.setHistoryMessages(historyMessages);
        AskContext context = new AskContext();
        context.session = session;
        context.userMessage = userMessage;
        context.requestDto = requestDto;
        context.operatorName = operatorName;
        context.deepThinkingApplied = deepThinkingApplied;
        return context;
    }

    private ScQaSession createOrLoadSession(QaAskDto askDto, String operatorName) {
        ScQaSession session;
        if (askDto.getSessionId() == null) {
            session = new ScQaSession();
            session.setUserId(askDto.getUserId());
            session.setCourseId(askDto.getCourseId());
            session.setSessionTitle(StringUtils.isEmpty(askDto.getSessionTitle()) ? "新问答会话" : askDto.getSessionTitle());
            session.setSourceType("course");
            session.setStatus("0");
            session.setMessageCount(0);
            session.setLastMessagePreview("");
            session.setContextSummary("");
            session.setCreateBy(operatorName);
            scQaSessionService.insertScQaSession(session);
        } else {
            session = scQaSessionService.selectScQaSessionBySessionId(askDto.getSessionId());
            if (session == null) {
                throw new IllegalArgumentException("问答会话不存在");
            }
        }
        return session;
    }

    private ScQaMessage createUserMessage(Long sessionId, QaAskDto askDto, String operatorName) {
        ScQaMessage userMessage = new ScQaMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setRoleType("user");
        userMessage.setContentType(askDto.getImages() != null && !askDto.getImages().isEmpty() ? "mixed" : "text");
        userMessage.setContent(askDto.getQuestion());
        userMessage.setReferenceSource(buildAttachmentSource(askDto));
        userMessage.setAttachmentJson(buildAttachmentJson(askDto));
        userMessage.setTokenCount(0);
        userMessage.setLatencyMs(0);
        userMessage.setSensitiveFlag("0");
        userMessage.setCreateBy(operatorName);
        scQaMessageService.insertScQaMessage(userMessage);
        bindUploadedAttachments(askDto.getImages(), sessionId, userMessage.getMessageId(), operatorName);
        touchSessionAfterMessage(sessionId, askDto.getQuestion());
        return userMessage;
    }

    private String buildAttachmentSource(QaAskDto askDto) {
        if (askDto.getImages() == null || askDto.getImages().isEmpty()) {
            return "";
        }
        java.util.List<String> names = new java.util.ArrayList<>();
        askDto.getImages().forEach(item -> names.add(item.getName()));
        return "附件：" + String.join(", ", names);
    }

    private String buildAttachmentJson(QaAskDto askDto) {
        if (askDto.getImages() == null || askDto.getImages().isEmpty()) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(askDto.getImages());
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private String resolveSystemPrompt(QaAskDto askDto) {
        String systemPrompt = "你是智慧校园自主学习助手，请围绕课程学习、资源推荐、知识点理解与考试答疑做出简洁、专业的回答。";
        ScAiPromptTemplate promptQuery = new ScAiPromptTemplate();
        promptQuery.setBizType("qa");
        promptQuery.setStatus("0");
        List<ScAiPromptTemplate> promptList = scAiPromptTemplateService.selectScAiPromptTemplateList(promptQuery);
        if (!promptList.isEmpty() && StringUtils.isNotEmpty(promptList.get(0).getPromptContent())) {
            systemPrompt = promptList.get(0).getPromptContent();
        }
        if (Boolean.TRUE.equals(askDto.getDeepThinking())) {
            systemPrompt += "\n请先给出结构化分析，再给出明确结论与建议，必要时分点输出。";
            systemPrompt += "\n思考过程与最终回答默认使用简体中文表达。除非用户明确要求，否则不要使用英文进行内部分析或作答。";
        }
        if (askDto.getImages() != null && !askDto.getImages().isEmpty()) {
            systemPrompt += "\n如果用户上传了图片，请结合图片内容回答；若当前模型不支持视觉能力，请明确说明。";
        }
        systemPrompt += buildMarkdownOutputRules();
        return systemPrompt;
    }

    private String buildQaUserPrompt(QaAskDto askDto) {
        if (askDto == null) {
            return "";
        }
        String question = StringUtils.defaultString(askDto.getQuestion()).trim();
        String contextPrompt = StringUtils.defaultString(askDto.getContextPrompt()).trim();
        if (StringUtils.isEmpty(contextPrompt)) {
            return question;
        }
        return "以下是学习上下文，仅供理解问题使用，请不要逐字复述这些上下文标题或标签。\n"
                + contextPrompt
                + "\n\n请直接回答下面这个用户问题：\n"
                + question;
    }

    private String buildMarkdownOutputRules() {
        return "\n请使用规范、稳定、便于前端渲染的 GitHub Flavored Markdown 输出，并严格遵守以下要求："
                + "\n1. 不要输出字母 n、字符串 \\n、\\r\\n 来代替换行，必须直接输出真正的换行。"
                + "\n2. 只在必要时使用标题，标题必须使用 # 或 ##，且标题后空一行。"
                + "\n3. 列表每一项必须单独占一行，禁止写成“1.xxx 2.xxx”这种同一行多条列表。"
                + "\n4. 表格只在列数稳定、数据规整时使用，必须输出标准 Markdown 表格；如果信息不规整，请改用列表，不要强行拼表格。"
                + "\n5. 代码、JSON、配置示例必须使用 fenced code block，并标明语言，如 ```json。"
                + "\n6. 普通学习计划、说明文字、步骤描述，不要放进 ```plaintext``` 或其他代码块。"
                + "\n7. 不要输出 HTML 标签，不要混用 Markdown 和原始 HTML。"
                + "\n8. 段落之间保留空行，避免把标题、列表、表格、代码块和正文粘在同一行。"
                + "\n9. 如果用户明确需要图表，或当前回答适合用趋势/占比/对比图展示，可以输出 ECharts 容器，格式必须严格如下："
                + "\n:::echarts"
                + "\n```json"
                + "\n{ \"title\": { \"text\": \"示例\" }, \"xAxis\": { \"type\": \"category\", \"data\": [\"A\", \"B\"] }, \"yAxis\": { \"type\": \"value\" }, \"series\": [{ \"type\": \"bar\", \"data\": [1, 2] }] }"
                + "\n```"
                + "\n:::"
                + "\n其中 JSON 必须是合法的 ECharts option，不允许加注释或额外解释。";
    }

    private ScQaMessage saveAssistantMessage(ScQaSession session, AiChatResponseVo aiResponse, String operatorName) {
        ScQaMessage assistantMessage = new ScQaMessage();
        assistantMessage.setSessionId(session.getSessionId());
        assistantMessage.setRoleType("assistant");
        assistantMessage.setContentType("markdown");
        assistantMessage.setContent(aiResponse.getContent());
        assistantMessage.setReferenceSource(buildAssistantReferenceSource(aiResponse));
        assistantMessage.setModelName(aiResponse.getModelName());
        assistantMessage.setReasoningContent(aiResponse.getReasoningContent());
        assistantMessage.setTokenCount(aiResponse.getTokenUsed());
        assistantMessage.setLatencyMs(aiResponse.getDurationMs());
        assistantMessage.setSensitiveFlag("0");
        assistantMessage.setCreateBy(operatorName);
        scQaMessageService.insertScQaMessage(assistantMessage);
        touchSessionAfterMessage(session.getSessionId(), aiResponse.getContent());
        return assistantMessage;
    }

    private AiChatResponseVo normalizeAssistantResponse(AiChatResponseVo aiResponse) {
        if (aiResponse == null) {
            return null;
        }
        aiResponse.setContent(normalizeQaMarkdown(aiResponse.getContent()));
        aiResponse.setReasoningContent(normalizeQaMarkdown(aiResponse.getReasoningContent()));
        return aiResponse;
    }

    private AiChatResponseVo finalizeAssistantResponse(AiChatResponseVo aiResponse, String systemPrompt,
            boolean deepThinkingApplied) {
        return finalizeAssistantResponse(aiResponse, systemPrompt, deepThinkingApplied, "");
    }

    private AiChatResponseVo finalizeAssistantResponse(AiChatResponseVo aiResponse, String systemPrompt,
            boolean deepThinkingApplied, String streamedReasoningContent) {
        if (aiResponse == null) {
            return null;
        }
        String userFacingReasoning = resolveUserFacingReasoningContent(
                sanitizeReasoningContent(aiResponse.getReasoningContent(), systemPrompt),
                deepThinkingApplied);
        if (StringUtils.isEmpty(userFacingReasoning) && StringUtils.isNotEmpty(streamedReasoningContent)) {
            userFacingReasoning = streamedReasoningContent;
        }
        aiResponse.setReasoningContent(userFacingReasoning);
        return normalizeAssistantResponse(aiResponse);
    }

    private List<AiHistoryMessageDto> loadRecentHistoryMessages(Long sessionId, int limit) {
        ScQaMessage query = new ScQaMessage();
        query.setSessionId(sessionId);
        query.setDescOrder(Boolean.TRUE);
        query.setLimitCount(limit);
        List<ScQaMessage> historyList = scQaMessageService.selectScQaMessageList(query);
        if (historyList == null || historyList.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        java.util.Collections.reverse(historyList);
        return historyList.stream()
                .filter(item -> StringUtils.isNotEmpty(item.getContent()))
                .skip(Math.max(0, historyList.size() - limit))
                .map(item -> {
                    AiHistoryMessageDto dto = new AiHistoryMessageDto();
                    dto.setRole("assistant".equals(item.getRoleType()) ? "assistant" : "user");
                    dto.setContent(truncateHistoryContent(item.getContent()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void touchSessionAfterMessage(Long sessionId, String latestContent) {
        if (sessionId == null) {
            return;
        }
        ScQaSession update = new ScQaSession();
        update.setSessionId(sessionId);
        update.setMessageCount(scQaMessageService.countScQaMessageBySessionId(sessionId));
        update.setLastMessageTime(new java.util.Date());
        if (StringUtils.isNotEmpty(latestContent)) {
            update.setLastMessagePreview(buildPreview(latestContent));
        }
        scQaSessionService.updateScQaSession(update);
    }

    private void bindUploadedAttachments(List<com.smart.system.domain.dto.AiImageInputDto> images, Long sessionId,
            Long messageId, String operatorName) {
        if (images == null || images.isEmpty()) {
            return;
        }
        for (com.smart.system.domain.dto.AiImageInputDto image : images) {
            if (image == null || image.getAttachmentId() == null) {
                continue;
            }
            ScQaAttachment attachment = new ScQaAttachment();
            attachment.setAttachmentId(image.getAttachmentId());
            attachment.setSessionId(sessionId);
            attachment.setMessageId(messageId);
            attachment.setUpdateBy(operatorName);
            scQaAttachmentService.updateScQaAttachment(attachment);
        }
    }

    private void cleanupSessionAttachments(Long sessionId) {
        ScQaAttachment query = new ScQaAttachment();
        query.setSessionId(sessionId);
        List<ScQaAttachment> attachments = scQaAttachmentService.selectScQaAttachmentList(query);
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        for (ScQaAttachment attachment : attachments) {
            deleteAttachmentFile(attachment);
            scQaAttachmentService.deleteScQaAttachmentByAttachmentId(attachment.getAttachmentId());
        }
    }

    private void deleteAttachmentFile(ScQaAttachment attachment) {
        if (attachment == null || StringUtils.isEmpty(attachment.getFileName())) {
            return;
        }
        FileUtils.deleteFile(RuoYiConfig.getProfile() + FileUtils.stripPrefix(attachment.getFileName()));
    }

    private String buildPreview(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() > 120 ? normalized.substring(0, 120) : normalized;
    }

    private String truncateHistoryContent(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        String normalized = content.trim();
        if (normalized.length() <= QA_HISTORY_CONTENT_MAX_LENGTH) {
            return normalized;
        }
        return normalized.substring(0, QA_HISTORY_CONTENT_MAX_LENGTH) + "\n\n[历史上下文已截断]";
    }

    private boolean shouldEnableDeepThinking(QaAskDto askDto) {
        if (askDto == null || !Boolean.TRUE.equals(askDto.getDeepThinking())) {
            return false;
        }
        String question = StringUtils.trimToEmpty(askDto.getQuestion());
        if (StringUtils.isEmpty(question)) {
            return false;
        }
        return !SIMPLE_GREETING_PATTERN.matcher(question).matches();
    }

    private String sanitizeReasoningContent(String reasoningContent, String systemPrompt) {
        if (StringUtils.isEmpty(reasoningContent)) {
            return "";
        }
        String sanitized = reasoningContent.replace("\r\n", "\n").replace("\r", "\n");
        if (StringUtils.isNotEmpty(systemPrompt)) {
            sanitized = sanitized.replace(systemPrompt, "");
            String[] promptLines = systemPrompt.split("\n");
            for (String line : promptLines) {
                String trimmedLine = StringUtils.trim(line);
                if (trimmedLine.length() < 8) {
                    continue;
                }
                sanitized = sanitized.replace(trimmedLine, "");
            }
        }
        String[] paragraphs = sanitized.split("\\n{2,}");
        List<String> cleanedParagraphs = new ArrayList<>();
        for (String paragraph : paragraphs) {
            String normalizedParagraph = StringUtils.trim(paragraph);
            if (StringUtils.isEmpty(normalizedParagraph)) {
                continue;
            }
            if (isInternalReasoningParagraph(normalizedParagraph)) {
                continue;
            }
            cleanedParagraphs.add(normalizedParagraph);
        }
        sanitized = String.join("\n\n", cleanedParagraphs)
                .replaceAll("(?is)^(好的[,，]?我会遵循(?:以上|这些)?(?:系统)?要求[：:]?)", "")
                .replaceAll("(?is)(系统提示词|system prompt|prompt 模板|提示词内容)[：:].*$", "")
                .replaceAll("\n{3,}", "\n\n")
                .trim();
        return sanitized;
    }

    private String resolveUserFacingReasoningContent(String sanitizedReasoningContent, boolean deepThinkingApplied) {
        if (!deepThinkingApplied) {
            return "";
        }
        if (StringUtils.isNotEmpty(sanitizedReasoningContent)) {
            return sanitizedReasoningContent;
        }
        return "已完成深度思考，已省略内部系统指令。";
    }

    private boolean isInternalReasoningParagraph(String paragraph) {
        String normalized = StringUtils.trimToEmpty(paragraph).toLowerCase();
        if (StringUtils.isEmpty(normalized)) {
            return true;
        }
        return normalized.contains("github flavored markdown")
                || normalized.contains("markdown 输出")
                || normalized.contains("markdown")
                || normalized.contains("system prompt")
                || normalized.contains("prompt 模板")
                || normalized.contains("系统提示词")
                || normalized.contains("提示词内容")
                || normalized.contains("便于前端渲染")
                || normalized.contains("输出格式")
                || normalized.contains("输出规范")
                || normalized.contains("不要输出")
                || normalized.contains("html 标签")
                || normalized.contains("代码块")
                || normalized.contains("fenced code block")
                || normalized.contains("echarts")
                || normalized.contains("换行")
                || normalized.contains("我的职责")
                || normalized.contains("核心功能")
                || normalized.contains("智慧校园自主学习助手")
                || normalized.contains("构建响应")
                || normalized.contains("完整响应草案")
                || normalized.contains("输出内容")
                || normalized.contains("输出格式")
                || normalized.contains("角色设定");
    }

    private String buildAssistantReferenceSource(AiChatResponseVo aiResponse) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("modelName", aiResponse.getModelName());
        payload.put("reasoningContent", aiResponse.getReasoningContent());
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return "AI模型：" + aiResponse.getModelName();
        }
    }

    private void sendEvent(SseEmitter emitter, String eventName, Object payload) throws IOException {
        emitter.send(SseEmitter.event().name(eventName).data(payload));
    }

    private Map<String, Object> mapOf(Object... keyValues) {
        Map<String, Object> map = new HashMap<>();
        if (keyValues == null) {
            return map;
        }
        for (int index = 0; index + 1 < keyValues.length; index += 2) {
            Object key = keyValues[index];
            Object value = keyValues[index + 1];
            if (key != null) {
                map.put(String.valueOf(key), value);
            }
        }
        return map;
    }

    private String normalizeQaMarkdown(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        String normalized = normalizeQaMarkdownPrelude(content);
        Matcher matcher = CODE_FENCE_PATTERN.matcher(normalized);
        StringBuilder builder = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            builder.append(normalizeQaMarkdownSegment(normalized.substring(lastEnd, matcher.start())));
            builder.append(matcher.group());
            lastEnd = matcher.end();
        }
        builder.append(normalizeQaMarkdownSegment(normalized.substring(lastEnd)));
        return builder.toString()
                .replaceAll("\n{3,}", "\n\n")
                .trim();
    }

    private String normalizeQaMarkdownPrelude(String content) {
        String normalized = content.replace("\r\n", "\n").replace("\\n", "\n");
        String[] lines = normalized.split("\n", -1);
        StringBuilder lineBuilder = new StringBuilder();
        for (int index = 0; index < lines.length; index++) {
            if (index > 0) {
                lineBuilder.append("\n");
            }
            lineBuilder.append(normalizePreludeLine(lines[index]));
        }
        normalized = lineBuilder.toString();
        normalized = normalized.replaceAll("(:::\\s*echarts)\\s*```", "$1\n```");
        normalized = normalized.replaceAll("(^|\\n)(:::\\s*echarts)(?=[^\\n])", "$1$2\n");
        normalized = normalized.replaceAll("(```[A-Za-z0-9_-]+)(?=[^\\n])", "$1\n");
        normalized = normalized.replaceAll("(~~~[A-Za-z0-9_-]+)(?=[^\\n])", "$1\n");
        normalized = normalized.replaceAll("([}\\]])\\s*(```|~~~)", "$1\n$2");
        normalized = normalized.replaceAll("(```|~~~)\\s*:::", "$1\n:::");

        int echartsOpenCount = countMatches(normalized, "(?m)^:::\\s*echarts\\b.*$");
        int echartsCloseCount = countMatches(normalized, "(?m)^:::\\s*$");
        if (echartsOpenCount > echartsCloseCount) {
            normalized = normalized.trim();
            if (countMatches(normalized, "```") % 2 == 1) {
                normalized = normalized + "\n```";
            }
            normalized = normalized + "\n:::";
        }
        return normalized;
    }

    private String normalizePreludeLine(String line) {
        if (StringUtils.isEmpty(line)) {
            return line;
        }
        if (line.contains("|")) {
            return normalizeBrokenTableSeparatorLine(line);
        }
        String normalized = line;
        normalized = normalized.replaceAll(
                "([^\\nA-Za-z])nn(?=(?:#{1,6}|[-*+]|\\d+[\\.\\)\\uFF09]|:::\\s*echarts|```|~~~|第[一二三四五六七八九十]+阶段|[一二三四五六七八九十]+、|周[一二三四五六日天末]))",
                "$1\n\n");
        normalized = normalized.replaceAll(
                "([^\\nA-Za-z])n(?=(?:#{1,6}|[-*+]|\\d+[\\.\\)\\uFF09]|:::\\s*echarts|```|~~~|第[一二三四五六七八九十]+阶段|[一二三四五六七八九十]+、|周[一二三四五六日天末]))",
                "$1\n");
        return normalized;
    }

    private String normalizeQaMarkdownSegment(String segment) {
        if (StringUtils.isEmpty(segment)) {
            return "";
        }
        String normalized = segment;
        normalized = normalized.replaceAll(
                "^\\s*([^\\n#`]{2,40}?)(?=(?:##|###|[一二三四五六七八九十]+、|第[一二三四五六七八九十]+阶段))",
                "# $1\n\n");
        normalized = normalized.replaceAll("(^|\\n)([一二三四五六七八九十]+、[^\\n#]+)", "$1## $2");
        normalized = normalized.replaceAll("(^|\\n)(第[一二三四五六七八九十]+阶段[：:][^\\n#]+)", "$1### $2");
        normalized = normalized.replaceAll("([^\\n#])\\s*(#{1,6})([^\\s#\\n])", "$1\n\n$2 $3");
        normalized = normalized.replaceAll("(^|\\n)(#{1,6})([^\\s#\\n])", "$1$2 $3");
        normalized = normalized.replaceAll("([^\\n])\\s*(#{1,6}\\s+)", "$1\\n\\n$2");
        normalized = normalized.replaceAll("(\\*\\*[^*\\n]+\\*\\*)(\\|)", "$1\\n$2");
        normalized = normalized.replaceAll("([^\\n])(\\|[-: \\t|]{3,}\\|)", "$1\\n$2");
        normalized = normalized.replaceAll("(\\|[-: \\t|]{3,}\\|)([^\\n|])", "$1\\n$2");
        normalized = normalized.replaceAll("(?m)^\\s*#{1,6}\\s*$", "");
        String[] lines = normalized.split("\n", -1);
        StringBuilder lineBuilder = new StringBuilder();
        for (int index = 0; index < lines.length; index++) {
            if (index > 0) {
                lineBuilder.append("\n");
            }
            lineBuilder.append(normalizeQaMarkdownLine(lines[index]));
        }
        return lineBuilder.toString();
    }

    private String normalizeQaMarkdownLine(String line) {
        if (line == null) {
            return "";
        }
        if (line.contains("|")) {
            return normalizeBrokenTableSeparatorLine(line);
        }
        String normalized = line;
        normalized = normalized.replaceAll("^([-*+])(?=\\S)", "$1 ");
        normalized = normalized.replaceAll("([：:])\\s*([-*+])(?=\\S)", "$1\\n$2 ");
        normalized = normalized.replaceAll("([^\\n])\\s+([-*+])(?=[\\u4e00-\\u9fa5A-Za-z(（【])", "$1\\n$2 ");
        normalized = normalized.replaceAll("^(\\d+[\\.\\)\\uFF09])(?=\\S)", "$1 ");
        normalized = normalized.replaceAll("([：:])\\s*(\\d+[\\.\\)\\uFF09])(?=\\S)", "$1\\n$2 ");
        normalized = normalized.replaceAll("([：:])\\s*(\\d+[\\.\\)\\uFF09])\\s*", "$1\\n$2 ");
        normalized = normalized.replaceAll("([^\\n])(\\d+[\\.\\)\\uFF09])(?=[\\u4e00-\\u9fa5A-Za-z])", "$1\\n$2 ");
        normalized = normalized.replaceAll("([^\\n])\\s+(\\d+[\\.\\)\\uFF09])\\s+", "$1\\n$2 ");
        return normalized;
    }

    private int countMatches(String content, String regex) {
        if (StringUtils.isEmpty(content)) {
            return 0;
        }
        Matcher matcher = Pattern.compile(regex).matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private String normalizeBrokenTableSeparatorLine(String line) {
        if (!looksLikeBrokenTableSeparatorLine(line)) {
            return line;
        }
        List<String> cells = getPipeCells(line);
        List<String> normalizedCells = new ArrayList<>();
        for (String cell : cells) {
            normalizedCells.add(normalizeTableSeparatorCell(cell));
        }
        return "| " + String.join(" | ", normalizedCells) + " |";
    }

    private boolean looksLikeBrokenTableSeparatorLine(String line) {
        if (StringUtils.isEmpty(line) || !line.contains("|")) {
            return false;
        }
        List<String> cells = getPipeCells(line);
        int nonEmptyCellCount = 0;
        for (String cell : cells) {
            if (StringUtils.isEmpty(cell)) {
                continue;
            }
            nonEmptyCellCount++;
            if (!cell.matches("[\\s:n-]+") || !cell.contains("-")) {
                return false;
            }
        }
        return nonEmptyCellCount >= 2;
    }

    private List<String> getPipeCells(String line) {
        String normalized = StringUtils.defaultString(line).trim();
        if (normalized.startsWith("|")) {
            normalized = normalized.substring(1);
        }
        if (normalized.endsWith("|")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        String[] parts = normalized.split("\\|", -1);
        List<String> cells = new ArrayList<>();
        for (String part : parts) {
            cells.add(part == null ? "" : part.trim());
        }
        return cells;
    }

    private String normalizeTableSeparatorCell(String cell) {
        String compact = StringUtils.defaultString(cell).replace("n", "").replaceAll("\\s+", "");
        boolean hasLeadingColon = compact.startsWith(":");
        boolean hasTrailingColon = compact.endsWith(":");
        if (hasLeadingColon && hasTrailingColon) {
            return ":---:";
        }
        if (hasLeadingColon) {
            return ":---";
        }
        if (hasTrailingColon) {
            return "---:";
        }
        return "---";
    }

    private static class AskContext {
        private ScQaSession session;
        private ScQaMessage userMessage;
        private AiChatRequestDto requestDto;
        private String operatorName;
        private boolean deepThinkingApplied;
    }

    private static class ReasoningStreamState {
        private final StringBuilder pendingReasoning = new StringBuilder();
        private final StringBuilder visibleReasoning = new StringBuilder();

        private String appendAndCollectVisible(String chunk, String systemPrompt) {
            if (StringUtils.isEmpty(chunk)) {
                return "";
            }
            pendingReasoning.append(chunk);
            return drainVisible(systemPrompt, false);
        }

        private String flushRemaining(String systemPrompt) {
            return drainVisible(systemPrompt, true);
        }

        private String getVisibleReasoning() {
            return visibleReasoning.toString();
        }

        private String drainVisible(String systemPrompt, boolean flushAll) {
            String source = pendingReasoning.toString();
            int splitIndex = flushAll ? source.length() : findLastReasoningBoundary(source);
            if (splitIndex <= 0) {
                return "";
            }
            String stableChunk = source.substring(0, splitIndex);
            pendingReasoning.delete(0, splitIndex);
            String sanitizedChunk = sanitizeReasoningStreamChunk(stableChunk, systemPrompt);
            if (StringUtils.isEmpty(sanitizedChunk)) {
                return "";
            }
            visibleReasoning.append(sanitizedChunk);
            return sanitizedChunk;
        }

        private int findLastReasoningBoundary(String content) {
            for (int index = content.length() - 1; index >= 0; index--) {
                char current = content.charAt(index);
                if (current == '\n') {
                    if (index > 0 && content.charAt(index - 1) == '\n') {
                        return index + 1;
                    }
                    return index + 1;
                }
                if (current == '。' || current == '！' || current == '？' || current == '!' || current == '?'
                        || current == ';' || current == '；') {
                    return index + 1;
                }
            }
            return -1;
        }

        private String sanitizeReasoningStreamChunk(String content, String systemPrompt) {
            if (StringUtils.isEmpty(content)) {
                return "";
            }
            return sanitizeReasoningChunk(content, systemPrompt);
        }
    }

    private static String sanitizeReasoningChunk(String reasoningChunk, String systemPrompt) {
        if (StringUtils.isEmpty(reasoningChunk)) {
            return "";
        }
        String sanitized = reasoningChunk.replace("\r\n", "\n").replace("\r", "\n");
        if (StringUtils.isNotEmpty(systemPrompt)) {
            sanitized = sanitized.replace(systemPrompt, "");
            String[] promptLines = systemPrompt.split("\n");
            for (String line : promptLines) {
                String trimmedLine = StringUtils.trim(line);
                if (trimmedLine.length() < 8) {
                    continue;
                }
                sanitized = sanitized.replace(trimmedLine, "");
            }
        }
        sanitized = sanitized
                .replaceAll("(?is)(系统提示词|system prompt|prompt 模板|提示词内容)[：:].*$", "")
                .replaceAll("\n{3,}", "\n\n");
        return sanitized.trim().isEmpty() ? "" : sanitized;
    }
}
