package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScStudentStatusLog;
import com.smart.system.mapper.ScStudentStatusLogMapper;
import com.smart.system.service.IScStudentStatusLogService;

@Service
public class ScStudentStatusLogServiceImpl implements IScStudentStatusLogService {
    @Autowired
    private ScStudentStatusLogMapper scStudentStatusLogMapper;

    @Override
    public ScStudentStatusLog selectScStudentStatusLogByStatusLogId(Long statusLogId) {
        return scStudentStatusLogMapper.selectScStudentStatusLogByStatusLogId(statusLogId);
    }

    @Override
    public List<ScStudentStatusLog> selectScStudentStatusLogList(ScStudentStatusLog scStudentStatusLog) {
        return scStudentStatusLogMapper.selectScStudentStatusLogList(scStudentStatusLog);
    }
}
