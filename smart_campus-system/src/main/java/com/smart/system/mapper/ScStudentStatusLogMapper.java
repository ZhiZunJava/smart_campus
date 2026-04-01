package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScStudentStatusLog;

public interface ScStudentStatusLogMapper {
    ScStudentStatusLog selectScStudentStatusLogByStatusLogId(Long statusLogId);

    List<ScStudentStatusLog> selectScStudentStatusLogList(ScStudentStatusLog scStudentStatusLog);

    int insertScStudentStatusLog(ScStudentStatusLog scStudentStatusLog);
}
