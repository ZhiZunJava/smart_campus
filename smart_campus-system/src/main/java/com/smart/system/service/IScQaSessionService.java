package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScQaSession;

public interface IScQaSessionService {
    ScQaSession selectScQaSessionBySessionId(Long sessionId);

    List<ScQaSession> selectScQaSessionList(ScQaSession scQaSession);

    int insertScQaSession(ScQaSession scQaSession);

    int updateScQaSession(ScQaSession scQaSession);

    int deleteScQaSessionBySessionIds(Long[] sessionIds);

    int deleteScQaSessionBySessionId(Long sessionId);
}
