package com.smart.system.domain.dto;

/**
 * AI 对话请求 DTO
 *
 * @author Codex
 */
public class AiChatRequestDto
{
    private Long modelId;
    private String systemPrompt;
    private String userPrompt;

    public Long getModelId()
    {
        return modelId;
    }

    public void setModelId(Long modelId)
    {
        this.modelId = modelId;
    }

    public String getSystemPrompt()
    {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt)
    {
        this.systemPrompt = systemPrompt;
    }

    public String getUserPrompt()
    {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt)
    {
        this.userPrompt = userPrompt;
    }
}
