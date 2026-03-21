package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 题目主对象 sc_question_item
 */
public class ScQuestionItem extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long itemId;
    private Long courseId;
    private Long defaultCatalogId;
    private Long latestVersionId;
    private Integer currentVersionNo;
    private String questionType;
    private Integer difficultyLevel;
    private String sourceType;
    private Long sourceRefId;
    private String sourceBatchNo;
    private Long creatorUserId;
    private String status;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getDefaultCatalogId() {
        return defaultCatalogId;
    }

    public void setDefaultCatalogId(Long defaultCatalogId) {
        this.defaultCatalogId = defaultCatalogId;
    }

    public Long getLatestVersionId() {
        return latestVersionId;
    }

    public void setLatestVersionId(Long latestVersionId) {
        this.latestVersionId = latestVersionId;
    }

    public Integer getCurrentVersionNo() {
        return currentVersionNo;
    }

    public void setCurrentVersionNo(Integer currentVersionNo) {
        this.currentVersionNo = currentVersionNo;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceRefId() {
        return sourceRefId;
    }

    public void setSourceRefId(Long sourceRefId) {
        this.sourceRefId = sourceRefId;
    }

    public String getSourceBatchNo() {
        return sourceBatchNo;
    }

    public void setSourceBatchNo(String sourceBatchNo) {
        this.sourceBatchNo = sourceBatchNo;
    }

    public Long getCreatorUserId() {
        return creatorUserId;
    }

    public void setCreatorUserId(Long creatorUserId) {
        this.creatorUserId = creatorUserId;
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
                .append("itemId", getItemId())
                .append("courseId", getCourseId())
                .append("defaultCatalogId", getDefaultCatalogId())
                .append("latestVersionId", getLatestVersionId())
                .append("currentVersionNo", getCurrentVersionNo())
                .append("questionType", getQuestionType())
                .append("difficultyLevel", getDifficultyLevel())
                .append("sourceType", getSourceType())
                .append("sourceRefId", getSourceRefId())
                .append("sourceBatchNo", getSourceBatchNo())
                .append("creatorUserId", getCreatorUserId())
                .append("status", getStatus())
                .toString();
    }
}
