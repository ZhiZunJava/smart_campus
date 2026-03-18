package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQaSession;

public interface ScQaSessionMapper {
    ScQaSession selectScQaSessionBySessionId(Long sessionId);

    List<ScQaSession> selectScQaSessionList(ScQaSession scQaSession);

    int insertScQaSession(ScQaSession scQaSession);

    int updateScQaSession(ScQaSession scQaSession);

    int deleteScQaSessionBySessionId(Long sessionId);

    int deleteScQaSessionBySessionIds(Long[] sessionIds);
}
