package com.smart.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 题目版本对象 sc_question_item_version
 */
public class ScQuestionItemVersion extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long versionId;
    private Long itemId;
    private Integer versionNo;
    private Long chapterId;
    private Long knowledgePointId;
    private String stem;
    private String answer;
    private String analysis;
    private String materialContent;
    private String questionTags;
    private BigDecimal qualityScore;
    private String source;
    private String changeType;
    private String isCurrent;
    private Long aiModelId;
    private String aiPromptSnapshot;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(Long knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getMaterialContent() {
        return materialContent;
    }

    public void setMaterialContent(String materialContent) {
        this.materialContent = materialContent;
    }

    public String getQuestionTags() {
        return questionTags;
    }

    public void setQuestionTags(String questionTags) {
        this.questionTags = questionTags;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(String isCurrent) {
        this.isCurrent = isCurrent;
    }

    public Long getAiModelId() {
        return aiModelId;
    }

    public void setAiModelId(Long aiModelId) {
        this.aiModelId = aiModelId;
    }

    public String getAiPromptSnapshot() {
        return aiPromptSnapshot;
    }

    public void setAiPromptSnapshot(String aiPromptSnapshot) {
        this.aiPromptSnapshot = aiPromptSnapshot;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("versionId", getVersionId())
                .append("itemId", getItemId())
                .append("versionNo", getVersionNo())
                .append("chapterId", getChapterId())
                .append("knowledgePointId", getKnowledgePointId())
                .append("changeType", getChangeType())
                .append("isCurrent", getIsCurrent())
                .append("aiModelId", getAiModelId())
                .toString();
    }
}

