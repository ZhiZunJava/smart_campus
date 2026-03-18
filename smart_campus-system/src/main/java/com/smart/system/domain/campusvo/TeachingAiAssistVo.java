package com.smart.system.domain.campusvo;

import java.util.List;
import java.util.Map;

/**
 * 教学基础模块 AI 助手返回 VO
 */
public class TeachingAiAssistVo {
    private String moduleKey;
    private String actionType;
    private String assistantTitle;
    private Map<String, Object> formDraft;
    private List<String> highlights;
    private List<String> warnings;
    private List<String> tips;
    private String rawContent;

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

    public String getAssistantTitle() {
        return assistantTitle;
    }

    public void setAssistantTitle(String assistantTitle) {
        this.assistantTitle = assistantTitle;
    }

    public Map<String, Object> getFormDraft() {
        return formDraft;
    }

    public void setFormDraft(Map<String, Object> formDraft) {
        this.formDraft = formDraft;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }
}
