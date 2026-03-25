package com.smart.system.domain;

import com.smart.common.core.domain.BaseEntity;

public class ScResourceNode extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long nodeId;
    private Long courseId;
    private Long parentId;
    private String nodeName;
    private Integer sortNo;
    private String expandDefault;
    private String status;

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public String getExpandDefault() {
        return expandDefault;
    }

    public void setExpandDefault(String expandDefault) {
        this.expandDefault = expandDefault;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
