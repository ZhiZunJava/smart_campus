package com.smart.system.domain.dto;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

public class VerificationSubmitDto {
    @NotNull(message = "记录ID不能为空")
    private Long recordId;

    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @NotEmpty(message = "变更列表不能为空")
    private List<ChangeItem> changes;

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

    public List<ChangeItem> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeItem> changes) {
        this.changes = changes;
    }

    public static class ChangeItem {
        private String fieldName;
        private String newValue;

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
}
