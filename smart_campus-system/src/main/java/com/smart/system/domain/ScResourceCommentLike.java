package com.smart.system.domain;

import com.smart.common.core.domain.BaseEntity;

public class ScResourceCommentLike extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long likeId;
    private Long commentId;
    private Long userId;

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
