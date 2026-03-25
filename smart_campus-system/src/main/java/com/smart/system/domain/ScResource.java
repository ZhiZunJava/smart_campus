package com.smart.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 学习资源对象 sc_resource
 *
 * @author can
 */
public class ScResource extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long resourceId;
    private String resourceName;
    private String resourceType;
    private Long courseId;
    private Long chapterId;
    private Long nodeId;
    private Integer sortNo;
    private Long uploaderId;
    private String authorName;
    private String publisherName;
    private String targetUserType;
    private String fileUrl;
    private String coverUrl;
    private String contentText;
    private String summary;
    private String auditStatus;
    private Integer viewCount;
    private Integer downloadCount;
    private Integer favoriteCount;
    private String allowComment;
    private String allowRating;
    private String allowShare;
    private BigDecimal qualityScore;
    private BigDecimal avgRating;
    private Integer ratingCount;
    private Integer commentCount;
    private Integer shareCount;
    private String status;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public Long getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getTargetUserType() {
        return targetUserType;
    }

    public void setTargetUserType(String targetUserType) {
        this.targetUserType = targetUserType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(String allowComment) {
        this.allowComment = allowComment;
    }

    public String getAllowRating() {
        return allowRating;
    }

    public void setAllowRating(String allowRating) {
        this.allowRating = allowRating;
    }

    public String getAllowShare() {
        return allowShare;
    }

    public void setAllowShare(String allowShare) {
        this.allowShare = allowShare;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
    }

    public BigDecimal getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(BigDecimal avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
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
                .append("resourceId", getResourceId())
                .append("resourceName", getResourceName())
                .append("resourceType", getResourceType())
                .append("courseId", getCourseId())
                .append("chapterId", getChapterId())
                .append("nodeId", getNodeId())
                .append("sortNo", getSortNo())
                .append("uploaderId", getUploaderId())
                .append("authorName", getAuthorName())
                .append("publisherName", getPublisherName())
                .append("targetUserType", getTargetUserType())
                .append("fileUrl", getFileUrl())
                .append("coverUrl", getCoverUrl())
                .append("contentText", getContentText())
                .append("summary", getSummary())
                .append("auditStatus", getAuditStatus())
                .append("viewCount", getViewCount())
                .append("downloadCount", getDownloadCount())
                .append("favoriteCount", getFavoriteCount())
                .append("allowComment", getAllowComment())
                .append("allowRating", getAllowRating())
                .append("allowShare", getAllowShare())
                .append("qualityScore", getQualityScore())
                .append("avgRating", getAvgRating())
                .append("ratingCount", getRatingCount())
                .append("commentCount", getCommentCount())
                .append("shareCount", getShareCount())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
