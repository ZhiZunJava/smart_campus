package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScCourseSelectionRequest extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long requestId;
    private String requestNo;
    private Long studentUserId;
    private String studentName;
    private String studentNo;
    private Long classId;
    private String className;
    private Long targetClassCourseId;
    private String targetTeachingClassCode;
    private Long targetCourseId;
    private String targetCourseName;
    private Long targetClassId;
    private String targetClassName;
    private Long targetTeacherId;
    private String targetTeacherName;
    private Long termId;
    private String termName;
    private String requestType;
    private String requestReason;
    private String requestStatus;
    private String reviewRemark;
    private Long reviewUserId;
    private String reviewUserName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelTime;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public Long getStudentUserId() {
        return studentUserId;
    }

    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getTargetClassCourseId() {
        return targetClassCourseId;
    }

    public void setTargetClassCourseId(Long targetClassCourseId) {
        this.targetClassCourseId = targetClassCourseId;
    }

    public String getTargetTeachingClassCode() {
        return targetTeachingClassCode;
    }

    public void setTargetTeachingClassCode(String targetTeachingClassCode) {
        this.targetTeachingClassCode = targetTeachingClassCode;
    }

    public Long getTargetCourseId() {
        return targetCourseId;
    }

    public void setTargetCourseId(Long targetCourseId) {
        this.targetCourseId = targetCourseId;
    }

    public String getTargetCourseName() {
        return targetCourseName;
    }

    public void setTargetCourseName(String targetCourseName) {
        this.targetCourseName = targetCourseName;
    }

    public Long getTargetClassId() {
        return targetClassId;
    }

    public void setTargetClassId(Long targetClassId) {
        this.targetClassId = targetClassId;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public Long getTargetTeacherId() {
        return targetTeacherId;
    }

    public void setTargetTeacherId(Long targetTeacherId) {
        this.targetTeacherId = targetTeacherId;
    }

    public String getTargetTeacherName() {
        return targetTeacherName;
    }

    public void setTargetTeacherName(String targetTeacherName) {
        this.targetTeacherName = targetTeacherName;
    }

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }

    public Long getReviewUserId() {
        return reviewUserId;
    }

    public void setReviewUserId(Long reviewUserId) {
        this.reviewUserId = reviewUserId;
    }

    public String getReviewUserName() {
        return reviewUserName;
    }

    public void setReviewUserName(String reviewUserName) {
        this.reviewUserName = reviewUserName;
    }

    public Date getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime) {
        this.reviewTime = reviewTime;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("requestId", getRequestId())
                .append("requestNo", getRequestNo())
                .append("studentUserId", getStudentUserId())
                .append("studentName", getStudentName())
                .append("studentNo", getStudentNo())
                .append("classId", getClassId())
                .append("className", getClassName())
                .append("targetClassCourseId", getTargetClassCourseId())
                .append("targetTeachingClassCode", getTargetTeachingClassCode())
                .append("targetCourseId", getTargetCourseId())
                .append("targetCourseName", getTargetCourseName())
                .append("targetClassId", getTargetClassId())
                .append("targetClassName", getTargetClassName())
                .append("targetTeacherId", getTargetTeacherId())
                .append("targetTeacherName", getTargetTeacherName())
                .append("termId", getTermId())
                .append("termName", getTermName())
                .append("requestType", getRequestType())
                .append("requestReason", getRequestReason())
                .append("requestStatus", getRequestStatus())
                .append("reviewRemark", getReviewRemark())
                .append("reviewUserId", getReviewUserId())
                .append("reviewUserName", getReviewUserName())
                .append("reviewTime", getReviewTime())
                .append("cancelTime", getCancelTime())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
