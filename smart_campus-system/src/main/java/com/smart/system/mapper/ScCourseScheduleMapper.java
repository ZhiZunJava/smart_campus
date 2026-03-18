package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScCourseSchedule;

public interface ScCourseScheduleMapper {
    ScCourseSchedule selectScCourseScheduleByScheduleId(Long scheduleId);

    List<ScCourseSchedule> selectScCourseScheduleList(ScCourseSchedule scCourseSchedule);

    int insertScCourseSchedule(ScCourseSchedule scCourseSchedule);

    int updateScCourseSchedule(ScCourseSchedule scCourseSchedule);

    int deleteScCourseScheduleByScheduleId(Long scheduleId);

    int deleteScCourseScheduleByScheduleIds(Long[] scheduleIds);
}
