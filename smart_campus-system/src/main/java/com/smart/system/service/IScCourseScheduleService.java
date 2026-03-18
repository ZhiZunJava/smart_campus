package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.domain.campusvo.CourseScheduleAutoArrangeVo;
import com.smart.system.domain.dto.CourseScheduleAutoArrangeDto;

public interface IScCourseScheduleService {
    ScCourseSchedule selectScCourseScheduleByScheduleId(Long scheduleId);

    List<ScCourseSchedule> selectScCourseScheduleList(ScCourseSchedule scCourseSchedule);

    Map<String, Object> checkClassroomConflict(ScCourseSchedule scCourseSchedule);

    Map<String, Object> checkScheduleConflict(ScCourseSchedule scCourseSchedule);

    CourseScheduleAutoArrangeVo autoArrangeCourseSchedule(CourseScheduleAutoArrangeDto request, String operator);

    int insertScCourseSchedule(ScCourseSchedule scCourseSchedule);

    int updateScCourseSchedule(ScCourseSchedule scCourseSchedule);

    int deleteScCourseScheduleByScheduleIds(Long[] scheduleIds);

    int deleteScCourseScheduleByScheduleId(Long scheduleId);
}
