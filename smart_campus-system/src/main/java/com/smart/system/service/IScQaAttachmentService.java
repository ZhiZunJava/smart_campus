package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScQaAttachment;

public interface IScQaAttachmentService {
    ScQaAttachment selectScQaAttachmentByAttachmentId(Long attachmentId);

    List<ScQaAttachment> selectScQaAttachmentList(ScQaAttachment scQaAttachment);

    int insertScQaAttachment(ScQaAttachment scQaAttachment);

    int updateScQaAttachment(ScQaAttachment scQaAttachment);

    int deleteScQaAttachmentByAttachmentId(Long attachmentId);
}
