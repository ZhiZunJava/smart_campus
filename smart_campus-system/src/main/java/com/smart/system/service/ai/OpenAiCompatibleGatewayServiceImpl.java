package com.smart.system.service.ai;

import java.io.IOException;
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
        requestDto.setSystemPrompt("You are a concise assistant.");
        requestDto.setUserPrompt(StringUtils.isEmpty(testDto.getPrompt()) ? "请只回复：连接正常" : testDto.getPrompt());
        return chat(requestDto);
    }

    @Override
    public AiChatResponseVo chat(AiChatRequestDto requestDto)
    {
        if (requestDto == null || requestDto.getModelId() == null)
        {
            throw new ServiceException("模型ID不能为空");
        }
        if (StringUtils.isEmpty(requestDto.getUserPrompt()))
        {
            throw new ServiceException("用户提示词不能为空");
        }

        ScAiModelConfig modelConfig = scAiModelConfigService.selectScAiModelConfigByModelId(requestDto.getModelId());
        if (modelConfig == null)
        {
            throw new ServiceException("AI 模型配置不存在");
        }
        if (StringUtils.isEmpty(modelConfig.getBaseUrl()) || StringUtils.isEmpty(modelConfig.getApiKey()) || StringUtils.isEmpty(modelConfig.getModelName()))
        {
            throw new ServiceException("AI 模型配置不完整，请检查地址、密钥和模型名称");
        }

        long start = System.currentTimeMillis();
        ScAiTaskLog taskLog = new ScAiTaskLog();
        taskLog.setBizType("ai_chat");
        taskLog.setBizId(requestDto.getModelId());
        taskLog.setModelId(modelConfig.getModelId());
        taskLog.setTaskStatus("RUNNING");

        try
        {
            String payload = buildChatPayload(modelConfig.getModelName(), requestDto.getSystemPrompt(), requestDto.getUserPrompt());
            taskLog.setRequestPayload(payload);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildChatUrl(modelConfig.getBaseUrl())))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + modelConfig.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            taskLog.setResponsePayload(response.body());
            taskLog.setDurationMs((int) (System.currentTimeMillis() - start));

            if (response.statusCode() < 200 || response.statusCode() >= 300)
            {
                taskLog.setTaskStatus("FAILED");
                taskLog.setErrorMsg("HTTP状态码异常: " + response.statusCode());
                scAiTaskLogService.insertScAiTaskLog(taskLog);
                throw new ServiceException("模型调用失败，HTTP状态码：" + response.statusCode());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String content = parseContent(root);
            int totalTokens = parseTokens(root);
            taskLog.setTokenUsed(totalTokens);
            taskLog.setTaskStatus("SUCCESS");
            scAiTaskLogService.insertScAiTaskLog(taskLog);

            AiChatResponseVo vo = new AiChatResponseVo();
            vo.setModelId(modelConfig.getModelId());
            vo.setModelName(modelConfig.getModelName());
            vo.setContent(content);
            vo.setTokenUsed(totalTokens);
            vo.setDurationMs(taskLog.getDurationMs());
            vo.setTaskStatus("SUCCESS");
            return vo;
        }
        catch (IOException | InterruptedException e)
        {
            taskLog.setTaskStatus("FAILED");
            taskLog.setErrorMsg(e.getMessage());
            taskLog.setDurationMs((int) (System.currentTimeMillis() - start));
            scAiTaskLogService.insertScAiTaskLog(taskLog);
            Thread.currentThread().interrupt();
            throw new ServiceException("AI 模型调用异常: " + e.getMessage());
        }
    }

    private String buildChatPayload(String modelName, String systemPrompt, String userPrompt) throws IOException
    {
        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("temperature", BigDecimal.valueOf(0.3));
        body.put("max_tokens", 512);
        List<Map<String, String>> messages = new ArrayList<>();
        if (StringUtils.isNotEmpty(systemPrompt))
        {
            messages.add(message("system", systemPrompt));
        }
        messages.add(message("user", userPrompt));
        body.put("messages", messages);
        return objectMapper.writeValueAsString(body);
    }

    private Map<String, String> message(String role, String content)
    {
        Map<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("content", content);
        return map;
    }

    private String buildChatUrl(String baseUrl)
    {
        String normalized = baseUrl.trim();
        if (normalized.endsWith("/chat/completions"))
        {
            return normalized;
        }
        if (normalized.endsWith("/v1"))
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

    private int parseTokens(JsonNode root)
    {
        return root.path("usage").path("total_tokens").asInt(0);
    }
}
