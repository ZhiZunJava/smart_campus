package com.smart.system.domain.campusvo;

import java.util.ArrayList;
import java.util.List;

public class ResourceNodeTreeVo {
    private Long nodeId;
    private Long courseId;
    private Long parentId;
    private String nodeName;
    private Integer sortNo;
    private Integer articleCount;
    private List<ResourceNodeTreeVo> children = new ArrayList<>();

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

    public Integer getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
    }

    public List<ResourceNodeTreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceNodeTreeVo> children) {
        this.children = children;
    }
}
