package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScAiTaskLog;
import com.smart.system.mapper.ScAiTaskLogMapper;
import com.smart.system.service.IScAiTaskLogService;

@Service
public class ScAiTaskLogServiceImpl implements IScAiTaskLogService {
    @Autowired
    private ScAiTaskLogMapper scAiTaskLogMapper;

    public ScAiTaskLog selectScAiTaskLogByTaskId(Long taskId) {
        return scAiTaskLogMapper.selectScAiTaskLogByTaskId(taskId);
    }

    public List<ScAiTaskLog> selectScAiTaskLogList(ScAiTaskLog scAiTaskLog) {
        return scAiTaskLogMapper.selectScAiTaskLogList(scAiTaskLog);
    }

    public int insertScAiTaskLog(ScAiTaskLog scAiTaskLog) {
        return scAiTaskLogMapper.insertScAiTaskLog(scAiTaskLog);
    }

    public int updateScAiTaskLog(ScAiTaskLog scAiTaskLog) {
        return scAiTaskLogMapper.updateScAiTaskLog(scAiTaskLog);
    }

    public int deleteScAiTaskLogByTaskIds(Long[] taskIds) {
        return scAiTaskLogMapper.deleteScAiTaskLogByTaskIds(taskIds);
    }

    public int deleteScAiTaskLogByTaskId(Long taskId) {
        return scAiTaskLogMapper.deleteScAiTaskLogByTaskId(taskId);
    }
}
