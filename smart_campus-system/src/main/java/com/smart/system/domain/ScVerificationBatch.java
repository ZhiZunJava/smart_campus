package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.common.core.domain.BaseEntity;

/**
 * 学籍核对批次对象 sc_verification_batch
 *
 * @author can
 */
public class ScVerificationBatch extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long batchId;

    private String batchName;

    private String batchNo;

    private String description;

    /** 可编辑字段JSON数组 */
    private String editableFields;

    /** 范围类型（ALL/GRADE/CLASS） */
    private String scopeType;

    private Long scopeGradeId;

    private Long scopeClassId;

    /** 批次状态（DRAFT/ACTIVE/CLOSED） */
    private String batchStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /* ---- 非持久化辅助字段，用于列表展示 ---- */
    /** 总记录数 */
    private transient Integer totalCount;
    /** 已确认数 */
    private transient Integer confirmedCount;
    /** 待审核数 */
    private transient Integer pendingReviewCount;
    /** 已通过数 */
    private transient Integer approvedCount;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEditableFields() {
        return editableFields;
    }

    public void setEditableFields(String editableFields) {
        this.editableFields = editableFields;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public Long getScopeGradeId() {
        return scopeGradeId;
    }

    public void setScopeGradeId(Long scopeGradeId) {
        this.scopeGradeId = scopeGradeId;
    }

    public Long getScopeClassId() {
        return scopeClassId;
    }

    public void setScopeClassId(Long scopeClassId) {
        this.scopeClassId = scopeClassId;
    }

    public String getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getConfirmedCount() {
        return confirmedCount;
    }

    public void setConfirmedCount(Integer confirmedCount) {
        this.confirmedCount = confirmedCount;
    }

    public Integer getPendingReviewCount() {
        return pendingReviewCount;
    }

    public void setPendingReviewCount(Integer pendingReviewCount) {
        this.pendingReviewCount = pendingReviewCount;
    }

    public Integer getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(Integer approvedCount) {
        this.approvedCount = approvedCount;
    }
}
