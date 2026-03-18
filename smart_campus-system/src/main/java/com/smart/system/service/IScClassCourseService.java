package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScClassCourse;

public interface IScClassCourseService {
    ScClassCourse selectScClassCourseById(Long id);

    List<ScClassCourse> selectScClassCourseList(ScClassCourse scClassCourse);

    int insertScClassCourse(ScClassCourse scClassCourse);

    int updateScClassCourse(ScClassCourse scClassCourse);

    int deleteScClassCourseByIds(Long[] ids);

    int deleteScClassCourseById(Long id);
}
