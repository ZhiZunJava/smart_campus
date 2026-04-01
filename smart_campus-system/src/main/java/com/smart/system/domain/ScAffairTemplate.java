package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScAffairTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long templateId;
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    private String templateCode;
    private String templateName;
    private String businessCode;
    private String businessName;
    private String audienceRoles;
    private String formSchemaJson;
    private String workflowSchemaJson;
    private String allowAttachment;
    private String allowRevoke;
    private String applyScopeJson;
    private String businessRulesJson;
    private String targetStatusCode;
    private String targetStatusName;
    private Integer sortOrder;
    private String status;

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAudienceRoles() {
        return audienceRoles;
    }

    public void setAudienceRoles(String audienceRoles) {
        this.audienceRoles = audienceRoles;
    }

    public String getFormSchemaJson() {
        return formSchemaJson;
    }

    public void setFormSchemaJson(String formSchemaJson) {
        this.formSchemaJson = formSchemaJson;
    }

    public String getWorkflowSchemaJson() {
        return workflowSchemaJson;
    }

    public void setWorkflowSchemaJson(String workflowSchemaJson) {
        this.workflowSchemaJson = workflowSchemaJson;
    }

    public String getAllowAttachment() {
        return allowAttachment;
    }

    public void setAllowAttachment(String allowAttachment) {
        this.allowAttachment = allowAttachment;
    }

    public String getAllowRevoke() {
        return allowRevoke;
    }

    public void setAllowRevoke(String allowRevoke) {
        this.allowRevoke = allowRevoke;
    }

    public String getApplyScopeJson() {
        return applyScopeJson;
    }

    public void setApplyScopeJson(String applyScopeJson) {
        this.applyScopeJson = applyScopeJson;
    }

    public String getBusinessRulesJson() {
        return businessRulesJson;
    }

    public void setBusinessRulesJson(String businessRulesJson) {
        this.businessRulesJson = businessRulesJson;
    }

    public String getTargetStatusCode() {
        return targetStatusCode;
    }

    public void setTargetStatusCode(String targetStatusCode) {
        this.targetStatusCode = targetStatusCode;
    }

    public String getTargetStatusName() {
        return targetStatusName;
    }

    public void setTargetStatusName(String targetStatusName) {
        this.targetStatusName = targetStatusName;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("templateId", getTemplateId())
                .append("categoryId", getCategoryId())
                .append("categoryCode", getCategoryCode())
                .append("categoryName", getCategoryName())
                .append("templateCode", getTemplateCode())
                .append("templateName", getTemplateName())
                .append("businessCode", getBusinessCode())
                .append("businessName", getBusinessName())
                .append("audienceRoles", getAudienceRoles())
                .append("formSchemaJson", getFormSchemaJson())
                .append("workflowSchemaJson", getWorkflowSchemaJson())
                .append("allowAttachment", getAllowAttachment())
                .append("allowRevoke", getAllowRevoke())
                .append("applyScopeJson", getApplyScopeJson())
                .append("businessRulesJson", getBusinessRulesJson())
                .append("targetStatusCode", getTargetStatusCode())
                .append("targetStatusName", getTargetStatusName())
                .append("sortOrder", getSortOrder())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
