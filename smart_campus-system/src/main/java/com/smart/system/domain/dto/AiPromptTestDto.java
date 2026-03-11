package com.smart.system.domain.dto;

/**
 * Prompt 测试 DTO
 *
 * @author Codex
 */
public class AiPromptTestDto
{
    private Long modelId;
    private Long templateId;
    private String userPrompt;

    public Long getModelId()
    {
        return modelId;
    }

    public void setModelId(Long modelId)
    {
        this.modelId = modelId;
    }

    public Long getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Long templateId)
    {
        this.templateId = templateId;
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
