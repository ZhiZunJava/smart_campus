package com.smart.system.domain.dto;

/**
 * AI 历史上下文消息 DTO
 */
public class AiHistoryMessageDto {
    private String role;
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
