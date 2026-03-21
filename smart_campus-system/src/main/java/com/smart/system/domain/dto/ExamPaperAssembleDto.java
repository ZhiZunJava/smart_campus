package com.smart.system.domain.dto;

import java.util.List;

/**
 * 智能组卷 DTO
 */
public class ExamPaperAssembleDto {
    private String paperName;
    private Long courseId;
    private String paperType;
    private Integer durationMinutes;
    private String publishStatus;
    private String status;
    private String remark;
    private Boolean savePaper;
    private List<PaperAssembleRuleDto> rules;

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getPaperType() {
        return paperType;
    }

    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getSavePaper() {
        return savePaper;
    }

    public void setSavePaper(Boolean savePaper) {
        this.savePaper = savePaper;
    }

    public List<PaperAssembleRuleDto> getRules() {
        return rules;
    }

    public void setRules(List<PaperAssembleRuleDto> rules) {
        this.rules = rules;
    }
}
