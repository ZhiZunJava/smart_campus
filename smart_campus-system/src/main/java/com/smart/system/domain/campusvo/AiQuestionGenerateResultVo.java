package com.smart.system.domain.campusvo;

import java.util.List;

/**
 * AI 智能出题结果 VO
 */
public class AiQuestionGenerateResultVo {
    private Integer generatedCount;
    private Integer savedCount;
    private Integer tokenUsed;
    private String rawContent;
    private List<QuestionDetailVo> questions;

    public Integer getGeneratedCount() {
        return generatedCount;
    }

    public void setGeneratedCount(Integer generatedCount) {
        this.generatedCount = generatedCount;
    }

    public Integer getSavedCount() {
        return savedCount;
    }

    public void setSavedCount(Integer savedCount) {
        this.savedCount = savedCount;
    }

    public Integer getTokenUsed() {
        return tokenUsed;
    }

    public void setTokenUsed(Integer tokenUsed) {
        this.tokenUsed = tokenUsed;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public List<QuestionDetailVo> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDetailVo> questions) {
        this.questions = questions;
    }
}
