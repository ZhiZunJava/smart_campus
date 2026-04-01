package com.smart.system.domain.dto;

import jakarta.validation.constraints.NotNull;

public class AffairRequestSubmitDto {
    @NotNull(message = "模板不能为空")
    private Long templateId;

    private String title;
    private String formDataJson;
    private String attachmentJson;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormDataJson() {
        return formDataJson;
    }

    public void setFormDataJson(String formDataJson) {
        this.formDataJson = formDataJson;
    }

    public String getAttachmentJson() {
        return attachmentJson;
    }

    public void setAttachmentJson(String attachmentJson) {
        this.attachmentJson = attachmentJson;
    }
}
