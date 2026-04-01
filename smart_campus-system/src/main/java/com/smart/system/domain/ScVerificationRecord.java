package com.smart.system.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.common.core.domain.BaseEntity;

/**
 * 学籍核对记录对象 sc_verification_record
 *
 * @author can
 */
public class ScVerificationRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long recordId;

    private Long batchId;

    private Long studentUserId;

    private String studentNo;

    private String studentName;

    /** 状态（PENDING/CONFIRMED/MODIFIED/UNDER_REVIEW/APPROVED/REJECTED） */
    private String recordStatus;

    /** 提交时档案快照JSON */
    private String snapshotJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    private Long reviewUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    private String reviewComment;

    /* ---- 非持久化辅助字段 ---- */
    /** 变更明细列表（详情查询时填充） */
    private transient List<ScVerificationChange> changes;
    /** 批次名称 */
    private transient String batchName;
    /** 班级名称 */
    private transient String className;
    /** 审核人姓名 */
    private transient String reviewUserName;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getSnapshotJson() {
        return snapshotJson;
    }

    public void setSnapshotJson(String snapshotJson) {
        this.snapshotJson = snapshotJson;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Long getReviewUserId() {
        return reviewUserId;
    }

    public void setReviewUserId(Long reviewUserId) {
        this.reviewUserId = reviewUserId;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public List<ScVerificationChange> getChanges() {
        return changes;
    }

    public void setChanges(List<ScVerificationChange> changes) {
        this.changes = changes;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getReviewUserName() {
        return reviewUserName;
    }

    public void setReviewUserName(String reviewUserName) {
        this.reviewUserName = reviewUserName;
    }
}
