package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQaMessage;

public interface ScQaMessageMapper {
    ScQaMessage selectScQaMessageByMessageId(Long messageId);

    List<ScQaMessage> selectScQaMessageList(ScQaMessage scQaMessage);

    int countScQaMessageBySessionId(Long sessionId);

    int insertScQaMessage(ScQaMessage scQaMessage);

    int updateScQaMessage(ScQaMessage scQaMessage);

    int deleteScQaMessageByMessageId(Long messageId);

    int deleteScQaMessageByMessageIds(Long[] messageIds);
}
