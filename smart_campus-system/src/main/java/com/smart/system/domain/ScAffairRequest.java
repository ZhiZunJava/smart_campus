package com.smart.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.annotation.Excel;
import com.smart.common.core.domain.BaseEntity;

public class ScAffairRequest extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long requestId;

    @Excel(name = "申请编号", sort = 1)
    private String requestNo;
    private Long categoryId;
    private String categoryCode;

    @Excel(name = "事务分类", sort = 2)
    private String categoryName;
    private Long templateId;
    private String templateCode;

    @Excel(name = "服务名称", sort = 3)
    private String templateName;
    private String businessCode;

    @Excel(name = "业务名称", sort = 4)
    private String businessName;
    private Long applicantUserId;

    @Excel(name = "申请人", sort = 5)
    private String applicantName;

    @Excel(name = "学号/工号", sort = 6)
    private String applicantNo;
    private String applicantUserType;
    private Long applicantDeptId;

    @Excel(name = "部门/学院", sort = 7)
    private String applicantDeptName;
    private Long gradeId;
    private String gradeName;
    private Long classId;

    @Excel(name = "班级", sort = 8)
    private String className;

    @Excel(name = "专业", sort = 9)
    private String major;
    private String currentStudentStatusCode;
    private String currentStudentStatusName;
    private String targetStudentStatusCode;
    private String targetStudentStatusName;

    @Excel(name = "申请标题", sort = 10)
    private String title;

    @Excel(name = "摘要", sort = 11)
    private String summaryText;

    @Excel(name = "状态", sort = 12)
    private String requestStatus;
    private Integer currentStepIndex;
    private String currentStepCode;

    @Excel(name = "当前节点", sort = 13)
    private String currentStepName;
    private String currentAssignmentType;
    private String currentReviewerRole;
    private Long currentReviewerUserId;

    @Excel(name = "当前审核人", sort = 14)
    private String currentReviewerName;

    @Excel(name = "提交时间", sort = 15, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submittedTime;

    @Excel(name = "完成时间", sort = 16, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
    private String lastAction;
    private String formDataJson;
    private String attachmentJson;

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

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
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

    public Long getApplicantUserId() {
        return applicantUserId;
    }

    public void setApplicantUserId(Long applicantUserId) {
        this.applicantUserId = applicantUserId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantNo() {
        return applicantNo;
    }

    public void setApplicantNo(String applicantNo) {
        this.applicantNo = applicantNo;
    }

    public String getApplicantUserType() {
        return applicantUserType;
    }

    public void setApplicantUserType(String applicantUserType) {
        this.applicantUserType = applicantUserType;
    }

    public Long getApplicantDeptId() {
        return applicantDeptId;
    }

    public void setApplicantDeptId(Long applicantDeptId) {
        this.applicantDeptId = applicantDeptId;
    }

    public String getApplicantDeptName() {
        return applicantDeptName;
    }

    public void setApplicantDeptName(String applicantDeptName) {
        this.applicantDeptName = applicantDeptName;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getCurrentStudentStatusCode() {
        return currentStudentStatusCode;
    }

    public void setCurrentStudentStatusCode(String currentStudentStatusCode) {
        this.currentStudentStatusCode = currentStudentStatusCode;
    }

    public String getCurrentStudentStatusName() {
        return currentStudentStatusName;
    }

    public void setCurrentStudentStatusName(String currentStudentStatusName) {
        this.currentStudentStatusName = currentStudentStatusName;
    }

    public String getTargetStudentStatusCode() {
        return targetStudentStatusCode;
    }

    public void setTargetStudentStatusCode(String targetStudentStatusCode) {
        this.targetStudentStatusCode = targetStudentStatusCode;
    }

    public String getTargetStudentStatusName() {
        return targetStudentStatusName;
    }

    public void setTargetStudentStatusName(String targetStudentStatusName) {
        this.targetStudentStatusName = targetStudentStatusName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Integer getCurrentStepIndex() {
        return currentStepIndex;
    }

    public void setCurrentStepIndex(Integer currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
    }

    public String getCurrentStepCode() {
        return currentStepCode;
    }

    public void setCurrentStepCode(String currentStepCode) {
        this.currentStepCode = currentStepCode;
    }

    public String getCurrentStepName() {
        return currentStepName;
    }

    public void setCurrentStepName(String currentStepName) {
        this.currentStepName = currentStepName;
    }

    public String getCurrentAssignmentType() {
        return currentAssignmentType;
    }

    public void setCurrentAssignmentType(String currentAssignmentType) {
        this.currentAssignmentType = currentAssignmentType;
    }

    public String getCurrentReviewerRole() {
        return currentReviewerRole;
    }

    public void setCurrentReviewerRole(String currentReviewerRole) {
        this.currentReviewerRole = currentReviewerRole;
    }

    public Long getCurrentReviewerUserId() {
        return currentReviewerUserId;
    }

    public void setCurrentReviewerUserId(Long currentReviewerUserId) {
        this.currentReviewerUserId = currentReviewerUserId;
    }

    public String getCurrentReviewerName() {
        return currentReviewerName;
    }

    public void setCurrentReviewerName(String currentReviewerName) {
        this.currentReviewerName = currentReviewerName;
    }

    public Date getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(Date submittedTime) {
        this.submittedTime = submittedTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getFormDataJson() {
        return formDataJson;
    }

    public void setFormDataJson(String formDataJson) {
        this.formDataJson = formDataJson;
    }

    public String getAttachmentJson() {
        return attachmentJson;
    }

    public void setAttachmentJson(String attachmentJson) {
        this.attachmentJson = attachmentJson;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("requestId", getRequestId())
                .append("requestNo", getRequestNo())
                .append("categoryId", getCategoryId())
                .append("categoryCode", getCategoryCode())
                .append("categoryName", getCategoryName())
                .append("templateId", getTemplateId())
                .append("templateCode", getTemplateCode())
                .append("templateName", getTemplateName())
                .append("businessCode", getBusinessCode())
                .append("businessName", getBusinessName())
                .append("applicantUserId", getApplicantUserId())
                .append("applicantName", getApplicantName())
                .append("applicantNo", getApplicantNo())
                .append("applicantUserType", getApplicantUserType())
                .append("applicantDeptId", getApplicantDeptId())
                .append("applicantDeptName", getApplicantDeptName())
                .append("gradeId", getGradeId())
                .append("gradeName", getGradeName())
                .append("classId", getClassId())
                .append("className", getClassName())
                .append("major", getMajor())
                .append("currentStudentStatusCode", getCurrentStudentStatusCode())
                .append("currentStudentStatusName", getCurrentStudentStatusName())
                .append("targetStudentStatusCode", getTargetStudentStatusCode())
                .append("targetStudentStatusName", getTargetStudentStatusName())
                .append("title", getTitle())
                .append("summaryText", getSummaryText())
                .append("requestStatus", getRequestStatus())
                .append("currentStepIndex", getCurrentStepIndex())
                .append("currentStepCode", getCurrentStepCode())
                .append("currentStepName", getCurrentStepName())
                .append("currentAssignmentType", getCurrentAssignmentType())
                .append("currentReviewerRole", getCurrentReviewerRole())
                .append("currentReviewerUserId", getCurrentReviewerUserId())
                .append("currentReviewerName", getCurrentReviewerName())
                .append("submittedTime", getSubmittedTime())
                .append("finishTime", getFinishTime())
                .append("lastAction", getLastAction())
                .append("formDataJson", getFormDataJson())
                .append("attachmentJson", getAttachmentJson())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
