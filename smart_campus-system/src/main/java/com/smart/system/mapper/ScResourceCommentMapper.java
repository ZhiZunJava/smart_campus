package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScResourceComment;

public interface ScResourceCommentMapper {
    List<ScResourceComment> selectScResourceCommentList(ScResourceComment scResourceComment);

    int insertScResourceComment(ScResourceComment scResourceComment);

    int updateScResourceComment(ScResourceComment scResourceComment);

    ScResourceComment selectScResourceCommentByCommentId(Long commentId);

    int countByResourceId(Long resourceId);
}
