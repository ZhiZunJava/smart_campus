package com.smart.system.domain.dto;

import java.util.List;
import java.util.Map;

/**
 * 教学基础模块 AI 助手请求 DTO
 */
public class TeachingAiAssistDto {
    private String moduleKey;
    private String actionType;
    private String userInstruction;
    private Map<String, Object> currentForm;
    private List<Map<String, Object>> selectedRows;
    private Map<String, Object> availableOptions;

    public String getModuleKey() {
        return moduleKey;
    }

    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getUserInstruction() {
        return userInstruction;
    }

    public void setUserInstruction(String userInstruction) {
        this.userInstruction = userInstruction;
    }

    public Map<String, Object> getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm(Map<String, Object> currentForm) {
        this.currentForm = currentForm;
    }

    public List<Map<String, Object>> getSelectedRows() {
        return selectedRows;
    }

    public void setSelectedRows(List<Map<String, Object>> selectedRows) {
        this.selectedRows = selectedRows;
    }

    public Map<String, Object> getAvailableOptions() {
        return availableOptions;
    }

    public void setAvailableOptions(Map<String, Object> availableOptions) {
        this.availableOptions = availableOptions;
    }
}
