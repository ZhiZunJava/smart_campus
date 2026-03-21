package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamBehaviorLog;

public interface ScExamBehaviorLogMapper {
    List<ScExamBehaviorLog> selectScExamBehaviorLogList(ScExamBehaviorLog scExamBehaviorLog);

    int insertScExamBehaviorLog(ScExamBehaviorLog scExamBehaviorLog);
}
