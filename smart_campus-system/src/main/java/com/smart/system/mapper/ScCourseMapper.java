package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScCourse;

public interface ScCourseMapper
{
    ScCourse selectScCourseByCourseId(Long courseId);

    List<ScCourse> selectScCourseList(ScCourse scCourse);

    int insertScCourse(ScCourse scCourse);

    int updateScCourse(ScCourse scCourse);

    int deleteScCourseByCourseId(Long courseId);

    int deleteScCourseByCourseIds(Long[] courseIds);
}
