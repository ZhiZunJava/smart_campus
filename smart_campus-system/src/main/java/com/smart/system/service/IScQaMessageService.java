package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScQaMessage;

public interface IScQaMessageService {
    ScQaMessage selectScQaMessageByMessageId(Long messageId);

    List<ScQaMessage> selectScQaMessageList(ScQaMessage scQaMessage);

    int countScQaMessageBySessionId(Long sessionId);

    int insertScQaMessage(ScQaMessage scQaMessage);

    int updateScQaMessage(ScQaMessage scQaMessage);

    int deleteScQaMessageByMessageIds(Long[] messageIds);

    int deleteScQaMessageByMessageId(Long messageId);
}
