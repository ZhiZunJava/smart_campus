package com.smart.system.domain.dto;

/**
 * AI 模型测试请求 DTO
 *
 * @author can
 */
public class AiModelTestDto {
    private Long modelId;
    private String prompt;

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
