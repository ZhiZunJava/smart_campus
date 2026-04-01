package com.smart.system.domain.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class VerificationBatchModifyDto {
    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @NotEmpty(message = "学生列表不能为空")
    private List<Long> studentUserIds;

    @NotBlank(message = "字段名不能为空")
    private String fieldName;

    @NotBlank(message = "新值不能为空")
    private String newValue;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public List<Long> getStudentUserIds() {
        return studentUserIds;
    }

    public void setStudentUserIds(List<Long> studentUserIds) {
        this.studentUserIds = studentUserIds;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
