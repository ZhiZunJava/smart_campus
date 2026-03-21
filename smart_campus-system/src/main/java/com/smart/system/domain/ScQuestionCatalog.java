package com.smart.system.domain;

import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 题库对象 sc_question_catalog
 */
public class ScQuestionCatalog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long catalogId;
    private String catalogName;
    private Long courseId;
    private Long gradeId;
    private Long ownerUserId;
    private String visibilityType;
    private String status;
    private List<ScQuestionCatalogScope> scopes;

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getVisibilityType() {
        return visibilityType;
    }

    public void setVisibilityType(String visibilityType) {
        this.visibilityType = visibilityType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ScQuestionCatalogScope> getScopes() {
        return scopes;
    }

    public void setScopes(List<ScQuestionCatalogScope> scopes) {
        this.scopes = scopes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("catalogId", getCatalogId())
                .append("catalogName", getCatalogName())
                .append("courseId", getCourseId())
                .append("gradeId", getGradeId())
                .append("ownerUserId", getOwnerUserId())
                .append("visibilityType", getVisibilityType())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}

