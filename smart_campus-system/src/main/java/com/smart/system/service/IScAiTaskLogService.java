package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScAiTaskLog;

public interface IScAiTaskLogService
{
    ScAiTaskLog selectScAiTaskLogByTaskId(Long taskId);
    List<ScAiTaskLog> selectScAiTaskLogList(ScAiTaskLog scAiTaskLog);
    int insertScAiTaskLog(ScAiTaskLog scAiTaskLog);
    int updateScAiTaskLog(ScAiTaskLog scAiTaskLog);
    int deleteScAiTaskLogByTaskIds(Long[] taskIds);
    int deleteScAiTaskLogByTaskId(Long taskId);
}
