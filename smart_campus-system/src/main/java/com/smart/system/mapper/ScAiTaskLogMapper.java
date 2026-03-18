package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScAiTaskLog;

public interface ScAiTaskLogMapper {
    ScAiTaskLog selectScAiTaskLogByTaskId(Long taskId);

    List<ScAiTaskLog> selectScAiTaskLogList(ScAiTaskLog scAiTaskLog);

    int insertScAiTaskLog(ScAiTaskLog scAiTaskLog);

    int updateScAiTaskLog(ScAiTaskLog scAiTaskLog);

    int deleteScAiTaskLogByTaskId(Long taskId);

    int deleteScAiTaskLogByTaskIds(Long[] taskIds);
}
