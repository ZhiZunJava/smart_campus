package com.smart.system.service.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScAiModelConfig;
import com.smart.system.domain.ScAiTaskLog;
import com.smart.system.domain.campusvo.AiChatResponseVo;
import com.smart.system.domain.dto.AiChatRequestDto;
import com.smart.system.domain.dto.AiImageInputDto;
import com.smart.system.domain.dto.AiModelTestDto;
import com.smart.system.service.IScAiModelConfigService;
import com.smart.system.service.IScAiTaskLogService;

@Service
public class OpenAiCompatibleGatewayServiceImpl implements IAiGatewayService
{
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    @Autowired
    private IScAiModelConfigService scAiModelConfigService;

    @Autowired
    private IScAiTaskLogService scAiTaskLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public AiChatResponseVo testModel(AiModelTestDto testDto)
    {
        AiChatRequestDto requestDto = new AiChatRequestDto();
        requestDto.setModelId(testDto.getModelId());
        requestDto.setBizType("model_test");
        requestDto.setSystemPrompt("You are a concise assistant.");
        requestDto.setUserPrompt(StringUtils.isEmpty(testDto.getPrompt()) ? "请只回复：连接正常" : testDto.getPrompt());
        requestDto.setStream(Boolean.FALSE);
        requestDto.setDeepThinking(Boolean.FALSE);
        return chat(requestDto);
    }

    @Override
    public AiChatResponseVo chat(AiChatRequestDto requestDto)
    {
        ExecutionContext context = prepareExecution(requestDto);
        return executeNormalChat(context);
    }

    @Override
    public AiChatResponseVo streamChat(AiChatRequestDto requestDto, Consumer<String> onChunk)
    {
        ExecutionContext context = prepareExecution(requestDto);
        context.requestDto.setStream(Boolean.TRUE);
        return executeStreamingChat(context, onChunk == null ? chunk -> { } : onChunk);
    }

    private ExecutionContext prepareExecution(AiChatRequestDto requestDto)
    {
        if (requestDto == null)
        {
            throw new ServiceException("模型请求不能为空");
        }
        if (StringUtils.isEmpty(requestDto.getUserPrompt()))
        {
            throw new ServiceException("用户提示词不能为空");
        }

        ScAiModelConfig modelConfig = scAiModelConfigService.resolveModel("chat", requestDto.getBizType(), requestDto.getModelId());
        String actualModelCode = resolveActualModelCode(modelConfig, requestDto.getDeepThinking(), hasImages(requestDto));
        if (StringUtils.isEmpty(modelConfig.getBaseUrl()) || StringUtils.isEmpty(modelConfig.getApiKey()) || StringUtils.isEmpty(actualModelCode))
        {
            throw new ServiceException("AI 模型配置不完整，请检查地址、密钥和模型编码");
        }
        if (hasImages(requestDto) && !"1".equals(modelConfig.getSupportVision()))
        {
            throw new ServiceException("当前模型未开启图片理解能力");
        }

        ExecutionContext context = new ExecutionContext();
        context.requestDto = requestDto;
        context.modelConfig = modelConfig;
        context.actualModelCode = actualModelCode;
        context.startedAt = System.currentTimeMillis();
        context.taskLog = new ScAiTaskLog();
        context.taskLog.setBizType(StringUtils.isEmpty(requestDto.getBizType()) ? "ai_chat" : requestDto.getBizType());
        context.taskLog.setBizId(modelConfig.getModelId());
        context.taskLog.setModelId(modelConfig.getModelId());
        context.taskLog.setTaskStatus("RUNNING");
        return context;
    }

    private AiChatResponseVo executeNormalChat(ExecutionContext context)
    {
        try
        {
            String payload = buildChatPayload(context.actualModelCode, context.requestDto);
            context.taskLog.setRequestPayload(payload);
            HttpRequest request = buildRequest(context.modelConfig, payload);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            context.taskLog.setResponsePayload(response.body());
            context.taskLog.setDurationMs((int) (System.currentTimeMillis() - context.startedAt));

            if (response.statusCode() < 200 || response.statusCode() >= 300)
            {
                failTask(context, "HTTP状态码异常: " + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String content = parseContent(root);
            int totalTokens = parseTokens(root);
            context.taskLog.setTokenUsed(totalTokens);
            context.taskLog.setTaskStatus("SUCCESS");
            scAiTaskLogService.insertScAiTaskLog(context.taskLog);
            return buildResponse(context, content, totalTokens);
        }
        catch (IOException e)
        {
            failTask(context, e.getMessage());
            throw new ServiceException("AI 模型调用异常: " + e.getMessage());
        }
        catch (InterruptedException e)
        {
            failTask(context, e.getMessage());
            Thread.currentThread().interrupt();
            throw new ServiceException("AI 模型调用异常: " + e.getMessage());
        }
    }

    private AiChatResponseVo executeStreamingChat(ExecutionContext context, Consumer<String> onChunk)
    {
        StringBuilder fullContent = new StringBuilder();
        try
        {
            String payload = buildChatPayload(context.actualModelCode, context.requestDto);
            context.taskLog.setRequestPayload(payload);
            HttpRequest request = buildRequest(context.modelConfig, payload);
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            context.taskLog.setDurationMs((int) (System.currentTimeMillis() - context.startedAt));

            if (response.statusCode() < 200 || response.statusCode() >= 300)
            {
                String body = readAll(response.body());
                context.taskLog.setResponsePayload(body);
                failTask(context, "HTTP状态码异常: " + response.statusCode());
            }

            int totalTokens = 0;
            StringBuilder rawSse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    rawSse.append(line).append("\n");
                    if (!line.startsWith("data:"))
                    {
                        continue;
                    }
                    String data = line.substring(5).trim();
                    if ("[DONE]".equals(data))
                    {
                        break;
                    }
                    if (StringUtils.isEmpty(data))
                    {
                        continue;
                    }
                    JsonNode root = objectMapper.readTree(data);
                    String delta = parseDeltaContent(root);
                    if (StringUtils.isNotEmpty(delta))
                    {
                        fullContent.append(delta);
                        onChunk.accept(delta);
                    }
                    totalTokens = parseTokens(root) > 0 ? parseTokens(root) : totalTokens;
                }
            }
            context.taskLog.setResponsePayload(rawSse.toString());
            context.taskLog.setTokenUsed(totalTokens);
            context.taskLog.setDurationMs((int) (System.currentTimeMillis() - context.startedAt));
            context.taskLog.setTaskStatus("SUCCESS");
            scAiTaskLogService.insertScAiTaskLog(context.taskLog);
            return buildResponse(context, fullContent.toString(), totalTokens);
        }
        catch (IOException e)
        {
            failTask(context, e.getMessage());
            throw new ServiceException("AI 模型流式调用异常: " + e.getMessage());
        }
        catch (InterruptedException e)
        {
            failTask(context, e.getMessage());
            Thread.currentThread().interrupt();
            throw new ServiceException("AI 模型流式调用异常: " + e.getMessage());
        }
    }

    private HttpRequest buildRequest(ScAiModelConfig modelConfig, String payload)
    {
        return HttpRequest.newBuilder()
            .uri(URI.create(buildChatUrl(modelConfig.getBaseUrl())))
            .timeout(Duration.ofSeconds(120))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + modelConfig.getApiKey())
            .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
            .build();
    }

    private AiChatResponseVo buildResponse(ExecutionContext context, String content, int totalTokens)
    {
        AiChatResponseVo vo = new AiChatResponseVo();
        vo.setModelId(context.modelConfig.getModelId());
        vo.setModelName(context.modelConfig.getModelName());
        vo.setContent(content);
        vo.setTokenUsed(totalTokens);
        vo.setDurationMs(context.taskLog.getDurationMs());
        vo.setTaskStatus("SUCCESS");
        return vo;
    }

    private void failTask(ExecutionContext context, String message)
    {
        context.taskLog.setTaskStatus("FAILED");
        context.taskLog.setErrorMsg(message);
        context.taskLog.setDurationMs((int) (System.currentTimeMillis() - context.startedAt));
        scAiTaskLogService.insertScAiTaskLog(context.taskLog);
        throw new ServiceException(message);
    }

    private boolean hasImages(AiChatRequestDto requestDto)
    {
        return requestDto.getImages() != null && !requestDto.getImages().isEmpty();
    }

    private String resolveActualModelCode(ScAiModelConfig modelConfig, Boolean deepThinking, boolean hasImages)
    {
        if (hasImages && StringUtils.isNotEmpty(modelConfig.getVisionModelCode()))
        {
            return modelConfig.getVisionModelCode();
        }
        if (Boolean.TRUE.equals(deepThinking) && "1".equals(modelConfig.getSupportReasoning()) && StringUtils.isNotEmpty(modelConfig.getReasoningModelCode()))
        {
            return modelConfig.getReasoningModelCode();
        }
        if (StringUtils.isNotEmpty(modelConfig.getModelCode()))
        {
            return modelConfig.getModelCode();
        }
        return modelConfig.getModelName();
    }

    private String buildChatPayload(String modelCode, AiChatRequestDto requestDto) throws IOException
    {
        Map<String, Object> body = new HashMap<>();
        body.put("model", modelCode);
        body.put("temperature", BigDecimal.valueOf(0.3));
        body.put("max_tokens", 1024);
        body.put("stream", Boolean.TRUE.equals(requestDto.getStream()));
        body.put("messages", buildMessages(requestDto));
        return objectMapper.writeValueAsString(body);
    }

    private List<Map<String, Object>> buildMessages(AiChatRequestDto requestDto)
    {
        List<Map<String, Object>> messages = new ArrayList<>();
        if (StringUtils.isNotEmpty(requestDto.getSystemPrompt()))
        {
            Map<String, Object> system = new HashMap<>();
            system.put("role", "system");
            system.put("content", requestDto.getSystemPrompt());
            messages.add(system);
        }

        Map<String, Object> user = new HashMap<>();
        user.put("role", "user");
        if (hasImages(requestDto))
        {
            List<Map<String, Object>> content = new ArrayList<>();
            Map<String, Object> textItem = new HashMap<>();
            textItem.put("type", "text");
            textItem.put("text", requestDto.getUserPrompt());
            content.add(textItem);
            for (AiImageInputDto image : requestDto.getImages())
            {
                if (StringUtils.isEmpty(image.getDataUrl()))
                {
                    continue;
                }
                Map<String, Object> imageItem = new HashMap<>();
                imageItem.put("type", "image_url");
                Map<String, Object> imageUrl = new HashMap<>();
                imageUrl.put("url", image.getDataUrl());
                imageItem.put("image_url", imageUrl);
                content.add(imageItem);
            }
            user.put("content", content);
        }
        else
        {
            user.put("content", requestDto.getUserPrompt());
        }
        messages.add(user);
        return messages;
    }

    private String buildChatUrl(String baseUrl)
    {
        String normalized = baseUrl.trim();
        if (normalized.endsWith("/chat/completions"))
        {
            return normalized;
        }
        if (normalized.endsWith("/v1") || normalized.endsWith("/v4") || normalized.endsWith("/api/v3") || normalized.endsWith("/compatible-mode/v1"))
        {
            return normalized + "/chat/completions";
        }
        if (normalized.endsWith("/"))
        {
            return normalized + "v1/chat/completions";
        }
        return normalized + "/v1/chat/completions";
    }

    private String parseContent(JsonNode root)
    {
        JsonNode choices = root.path("choices");
        if (choices.isArray() && !choices.isEmpty())
        {
            return choices.get(0).path("message").path("content").asText("");
        }
        return "";
    }

    private String parseDeltaContent(JsonNode root)
    {
        JsonNode choices = root.path("choices");
        if (choices.isArray() && !choices.isEmpty())
        {
            JsonNode delta = choices.get(0).path("delta");
            if (delta.has("content"))
            {
                return delta.path("content").asText("");
            }
        }
        return "";
    }

    private int parseTokens(JsonNode root)
    {
        return root.path("usage").path("total_tokens").asInt(0);
    }

    private String readAll(InputStream inputStream) throws IOException
    {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static class ExecutionContext
    {
        private AiChatRequestDto requestDto;
        private ScAiModelConfig modelConfig;
        private String actualModelCode;
        private long startedAt;
        private ScAiTaskLog taskLog;
    }
}
