package com.smart.system.domain.dto;

import java.util.List;

/**
 * AI 对话请求 DTO
 *
 * @author can
 */
public class AiChatRequestDto {
    private Long modelId;
    private String bizType;
    private String systemPrompt;
    private String userPrompt;
    private Boolean stream;
    private Boolean deepThinking;
    private List<AiImageInputDto> images;
    private List<AiHistoryMessageDto> historyMessages;

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt) {
        this.userPrompt = userPrompt;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public Boolean getDeepThinking() {
        return deepThinking;
    }

    public void setDeepThinking(Boolean deepThinking) {
        this.deepThinking = deepThinking;
    }

    public List<AiImageInputDto> getImages() {
        return images;
    }

    public void setImages(List<AiImageInputDto> images) {
        this.images = images;
    }

    public List<AiHistoryMessageDto> getHistoryMessages() {
        return historyMessages;
    }

    public void setHistoryMessages(List<AiHistoryMessageDto> historyMessages) {
        this.historyMessages = historyMessages;
    }
}
