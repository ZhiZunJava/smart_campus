package com.smart.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 题库权限范围对象 sc_question_catalog_scope
 */
public class ScQuestionCatalogScope extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long catalogId;
    private String scopeType;
    private Long scopeRefId;
    private String scopeRefName;
    private String permissionLevel;
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public Long getScopeRefId() {
        return scopeRefId;
    }

    public void setScopeRefId(Long scopeRefId) {
        this.scopeRefId = scopeRefId;
    }

    public String getScopeRefName() {
        return scopeRefName;
    }

    public void setScopeRefName(String scopeRefName) {
        this.scopeRefName = scopeRefName;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
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
                .append("id", getId())
                .append("catalogId", getCatalogId())
                .append("scopeType", getScopeType())
                .append("scopeRefId", getScopeRefId())
                .append("scopeRefName", getScopeRefName())
                .append("permissionLevel", getPermissionLevel())
                .append("status", getStatus())
                .toString();
    }
}
