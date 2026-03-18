package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * AI模型配置对象 sc_ai_model_config
 *
 * @author can
 */
public class ScAiModelConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long modelId;
    private String modelName;
    private String provider;
    private String baseUrl;
    private String apiKey;
    private String modelType;
    private String status;
    private String isDefault;
    private Integer priority;
    private String bizTypes;
    private String modelCode;
    private String reasoningModelCode;
    private String visionModelCode;
    private String supportStream;
    private String supportReasoning;
    private String supportVision;

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getBizTypes() {
        return bizTypes;
    }

    public void setBizTypes(String bizTypes) {
        this.bizTypes = bizTypes;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getReasoningModelCode() {
        return reasoningModelCode;
    }

    public void setReasoningModelCode(String reasoningModelCode) {
        this.reasoningModelCode = reasoningModelCode;
    }

    public String getVisionModelCode() {
        return visionModelCode;
    }

    public void setVisionModelCode(String visionModelCode) {
        this.visionModelCode = visionModelCode;
    }

    public String getSupportStream() {
        return supportStream;
    }

    public void setSupportStream(String supportStream) {
        this.supportStream = supportStream;
    }

    public String getSupportReasoning() {
        return supportReasoning;
    }

    public void setSupportReasoning(String supportReasoning) {
        this.supportReasoning = supportReasoning;
    }

    public String getSupportVision() {
        return supportVision;
    }

    public void setSupportVision(String supportVision) {
        this.supportVision = supportVision;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("modelId", getModelId())
                .append("modelName", getModelName())
                .append("provider", getProvider())
                .append("baseUrl", getBaseUrl())
                .append("modelCode", getModelCode())
                .append("reasoningModelCode", getReasoningModelCode())
                .append("visionModelCode", getVisionModelCode())
                .append("modelType", getModelType())
                .append("status", getStatus())
                .append("isDefault", getIsDefault())
                .append("priority", getPriority())
                .append("bizTypes", getBizTypes())
                .append("supportStream", getSupportStream())
                .append("supportReasoning", getSupportReasoning())
                .append("supportVision", getSupportVision())
                .toString();
    }
}
