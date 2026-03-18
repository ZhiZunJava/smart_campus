package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQaAttachment;

public interface ScQaAttachmentMapper {
    ScQaAttachment selectScQaAttachmentByAttachmentId(Long attachmentId);

    List<ScQaAttachment> selectScQaAttachmentList(ScQaAttachment scQaAttachment);

    int insertScQaAttachment(ScQaAttachment scQaAttachment);

    int updateScQaAttachment(ScQaAttachment scQaAttachment);

    int deleteScQaAttachmentByAttachmentId(Long attachmentId);
}
