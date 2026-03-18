package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScQaSession;
import com.smart.system.mapper.ScQaSessionMapper;
import com.smart.system.service.IScQaSessionService;

@Service
public class ScQaSessionServiceImpl implements IScQaSessionService {
    @Autowired
    private ScQaSessionMapper scQaSessionMapper;

    public ScQaSession selectScQaSessionBySessionId(Long sessionId) {
        return scQaSessionMapper.selectScQaSessionBySessionId(sessionId);
    }

    public List<ScQaSession> selectScQaSessionList(ScQaSession scQaSession) {
        return scQaSessionMapper.selectScQaSessionList(scQaSession);
    }

    public int insertScQaSession(ScQaSession scQaSession) {
        return scQaSessionMapper.insertScQaSession(scQaSession);
    }

    public int updateScQaSession(ScQaSession scQaSession) {
        return scQaSessionMapper.updateScQaSession(scQaSession);
    }

    public int deleteScQaSessionBySessionIds(Long[] sessionIds) {
        return scQaSessionMapper.deleteScQaSessionBySessionIds(sessionIds);
    }

    public int deleteScQaSessionBySessionId(Long sessionId) {
        return scQaSessionMapper.deleteScQaSessionBySessionId(sessionId);
    }
}
