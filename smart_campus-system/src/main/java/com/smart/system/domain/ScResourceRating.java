package com.smart.system.domain;

import com.smart.common.core.domain.BaseEntity;

public class ScResourceRating extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long ratingId;
    private Long resourceId;
    private Long userId;
    private Integer ratingScore;

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(Integer ratingScore) {
        this.ratingScore = ratingScore;
    }
}
