package com.smart.system.mapper;

import com.smart.system.domain.ScResourceCommentLike;

public interface ScResourceCommentLikeMapper {
    ScResourceCommentLike selectByCommentAndUser(Long commentId, Long userId);

    int insertScResourceCommentLike(ScResourceCommentLike scResourceCommentLike);

    int deleteByCommentAndUser(Long commentId, Long userId);

    int countByCommentId(Long commentId);
}
