package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScQaMessage;
import com.smart.system.mapper.ScQaMessageMapper;
import com.smart.system.service.IScQaMessageService;

@Service
public class ScQaMessageServiceImpl implements IScQaMessageService {
    @Autowired
    private ScQaMessageMapper scQaMessageMapper;

    public ScQaMessage selectScQaMessageByMessageId(Long messageId) {
        return scQaMessageMapper.selectScQaMessageByMessageId(messageId);
    }

    public List<ScQaMessage> selectScQaMessageList(ScQaMessage scQaMessage) {
        return scQaMessageMapper.selectScQaMessageList(scQaMessage);
    }

    public int insertScQaMessage(ScQaMessage scQaMessage) {
        return scQaMessageMapper.insertScQaMessage(scQaMessage);
    }

    public int updateScQaMessage(ScQaMessage scQaMessage) {
        return scQaMessageMapper.updateScQaMessage(scQaMessage);
    }

    public int deleteScQaMessageByMessageIds(Long[] messageIds) {
        return scQaMessageMapper.deleteScQaMessageByMessageIds(messageIds);
    }

    public int deleteScQaMessageByMessageId(Long messageId) {
        return scQaMessageMapper.deleteScQaMessageByMessageId(messageId);
    }
}
