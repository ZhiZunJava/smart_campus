package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScCourse;

public interface IScCourseService {
    ScCourse selectScCourseByCourseId(Long courseId);

    List<ScCourse> selectScCourseList(ScCourse scCourse);

    int insertScCourse(ScCourse scCourse);

    int updateScCourse(ScCourse scCourse);

    int deleteScCourseByCourseIds(Long[] courseIds);

    int deleteScCourseByCourseId(Long courseId);
}
