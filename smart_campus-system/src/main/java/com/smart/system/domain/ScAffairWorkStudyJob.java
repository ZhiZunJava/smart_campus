package com.smart.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

public class ScAffairWorkStudyJob extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long jobId;
    private String jobCode;
    private String jobName;
    private Long deptId;
    private String deptName;
    private Long termId;
    private String termName;
    private BigDecimal salaryAmount;
    private String salaryUnit;
    private String workPlace;
    private Integer weeklyHours;
    private Integer headcount;
    private Integer appliedCount;
    private String contactPerson;
    private String contactPhone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openEndTime;
    private String publishStatus;
    private String jobDesc;
    private String jobRequirements;

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public String getJobCode() { return jobCode; }
    public void setJobCode(String jobCode) { this.jobCode = jobCode; }
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }
    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }
    public BigDecimal getSalaryAmount() { return salaryAmount; }
    public void setSalaryAmount(BigDecimal salaryAmount) { this.salaryAmount = salaryAmount; }
    public String getSalaryUnit() { return salaryUnit; }
    public void setSalaryUnit(String salaryUnit) { this.salaryUnit = salaryUnit; }
    public String getWorkPlace() { return workPlace; }
    public void setWorkPlace(String workPlace) { this.workPlace = workPlace; }
    public Integer getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(Integer weeklyHours) { this.weeklyHours = weeklyHours; }
    public Integer getHeadcount() { return headcount; }
    public void setHeadcount(Integer headcount) { this.headcount = headcount; }
    public Integer getAppliedCount() { return appliedCount; }
    public void setAppliedCount(Integer appliedCount) { this.appliedCount = appliedCount; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public Date getOpenStartTime() { return openStartTime; }
    public void setOpenStartTime(Date openStartTime) { this.openStartTime = openStartTime; }
    public Date getOpenEndTime() { return openEndTime; }
    public void setOpenEndTime(Date openEndTime) { this.openEndTime = openEndTime; }
    public String getPublishStatus() { return publishStatus; }
    public void setPublishStatus(String publishStatus) { this.publishStatus = publishStatus; }
    public String getJobDesc() { return jobDesc; }
    public void setJobDesc(String jobDesc) { this.jobDesc = jobDesc; }
    public String getJobRequirements() { return jobRequirements; }
    public void setJobRequirements(String jobRequirements) { this.jobRequirements = jobRequirements; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("jobId", getJobId())
                .append("jobCode", getJobCode())
                .append("jobName", getJobName())
                .append("deptId", getDeptId())
                .append("deptName", getDeptName())
                .append("termId", getTermId())
                .append("termName", getTermName())
                .append("salaryAmount", getSalaryAmount())
                .append("salaryUnit", getSalaryUnit())
                .append("workPlace", getWorkPlace())
                .append("weeklyHours", getWeeklyHours())
                .append("headcount", getHeadcount())
                .append("appliedCount", getAppliedCount())
                .append("contactPerson", getContactPerson())
                .append("contactPhone", getContactPhone())
                .append("openStartTime", getOpenStartTime())
                .append("openEndTime", getOpenEndTime())
                .append("publishStatus", getPublishStatus())
                .append("jobDesc", getJobDesc())
                .append("jobRequirements", getJobRequirements())
                .append("remark", getRemark())
                .toString();
    }
}
