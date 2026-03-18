package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScClassCourse;

public interface ScClassCourseMapper {
    ScClassCourse selectScClassCourseById(Long id);

    List<ScClassCourse> selectScClassCourseList(ScClassCourse scClassCourse);

    int insertScClassCourse(ScClassCourse scClassCourse);

    int updateScClassCourse(ScClassCourse scClassCourse);

    int deleteScClassCourseById(Long id);

    int deleteScClassCourseByIds(Long[] ids);
}
