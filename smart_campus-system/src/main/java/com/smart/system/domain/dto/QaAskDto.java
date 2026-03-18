package com.smart.system.domain.dto;

import java.util.List;

/**
 * 问答提问 DTO
 *
 * @author can
 */
public class QaAskDto {
    private Long sessionId;
    private Long userId;
    private Long courseId;
    private Long modelId;
    private String sessionTitle;
    private String question;
    private Boolean stream;
    private Boolean deepThinking;
    private List<AiImageInputDto> images;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
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
}
