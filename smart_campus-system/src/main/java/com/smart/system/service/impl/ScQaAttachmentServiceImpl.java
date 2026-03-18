package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScQaAttachment;
import com.smart.system.mapper.ScQaAttachmentMapper;
import com.smart.system.service.IScQaAttachmentService;

@Service
public class ScQaAttachmentServiceImpl implements IScQaAttachmentService {
    @Autowired
    private ScQaAttachmentMapper scQaAttachmentMapper;

    @Override
    public ScQaAttachment selectScQaAttachmentByAttachmentId(Long attachmentId) {
        return scQaAttachmentMapper.selectScQaAttachmentByAttachmentId(attachmentId);
    }

    @Override
    public List<ScQaAttachment> selectScQaAttachmentList(ScQaAttachment scQaAttachment) {
        return scQaAttachmentMapper.selectScQaAttachmentList(scQaAttachment);
    }

    @Override
    public int insertScQaAttachment(ScQaAttachment scQaAttachment) {
        return scQaAttachmentMapper.insertScQaAttachment(scQaAttachment);
    }

    @Override
    public int updateScQaAttachment(ScQaAttachment scQaAttachment) {
        return scQaAttachmentMapper.updateScQaAttachment(scQaAttachment);
    }

    @Override
    public int deleteScQaAttachmentByAttachmentId(Long attachmentId) {
        return scQaAttachmentMapper.deleteScQaAttachmentByAttachmentId(attachmentId);
    }
}
