package com.smart.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.smart.common.core.domain.BaseEntity;

/**
 * 问答附件对象 sc_qa_attachment
 */
public class ScQaAttachment extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long attachmentId;
    private Long sessionId;
    private Long messageId;
    private Long userId;
    private String fileName;
    private String originalName;
    private String fileUrl;
    private String mimeType;
    private Long fileSize;
    private String storageType;
    private String status;
    private Date deleteTime;

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("attachmentId", getAttachmentId())
                .append("sessionId", getSessionId())
                .append("messageId", getMessageId())
                .append("userId", getUserId())
                .append("fileName", getFileName())
                .append("originalName", getOriginalName())
                .append("fileUrl", getFileUrl())
                .append("mimeType", getMimeType())
                .append("fileSize", getFileSize())
                .append("storageType", getStorageType())
                .append("status", getStatus())
                .append("deleteTime", getDeleteTime())
                .toString();
    }
}
