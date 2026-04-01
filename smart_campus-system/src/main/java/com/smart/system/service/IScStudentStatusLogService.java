package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScStudentStatusLog;

public interface IScStudentStatusLogService {
    ScStudentStatusLog selectScStudentStatusLogByStatusLogId(Long statusLogId);

    List<ScStudentStatusLog> selectScStudentStatusLogList(ScStudentStatusLog scStudentStatusLog);
}
