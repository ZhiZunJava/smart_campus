package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamSession;

public interface ScExamSessionMapper {
    ScExamSession selectScExamSessionBySessionId(Long sessionId);

    List<ScExamSession> selectScExamSessionList(ScExamSession scExamSession);

    int insertScExamSession(ScExamSession scExamSession);

    int updateScExamSession(ScExamSession scExamSession);
}

