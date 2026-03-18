package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScCourseStudent;

public interface IScCourseStudentService {
    ScCourseStudent selectScCourseStudentById(Long id);

    List<ScCourseStudent> selectScCourseStudentList(ScCourseStudent scCourseStudent);

    Map<String, Object> checkDuplicate(ScCourseStudent scCourseStudent);

    int insertScCourseStudent(ScCourseStudent scCourseStudent);

    int updateScCourseStudent(ScCourseStudent scCourseStudent);

    int deleteScCourseStudentByIds(Long[] ids);

    int deleteScCourseStudentById(Long id);
}
