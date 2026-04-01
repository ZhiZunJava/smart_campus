package com.smart.system.domain.dto;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class VerificationBatchCreateDto {
    private Long batchId;

    @NotBlank(message = "批次名称不能为空")
    private String batchName;

    private String description;

    @NotEmpty(message = "可编辑字段不能为空")
    private List<String> editableFields;

    @NotBlank(message = "范围类型不能为空")
    private String scopeType;

    private Long scopeGradeId;

    private Long scopeClassId;

    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @NotNull(message = "截止时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getEditableFields() {
        return editableFields;
    }

    public void setEditableFields(List<String> editableFields) {
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
}
