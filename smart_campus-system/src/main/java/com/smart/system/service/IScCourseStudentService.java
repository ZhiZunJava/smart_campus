package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.dto.CourseStudentBatchAddDto;

public interface IScCourseStudentService {
    ScCourseStudent selectScCourseStudentById(Long id);

    List<ScCourseStudent> selectScCourseStudentList(ScCourseStudent scCourseStudent);

    Map<String, Object> checkDuplicate(ScCourseStudent scCourseStudent);

    Map<String, Object> batchAddCourseStudents(CourseStudentBatchAddDto batchDto);

    int insertScCourseStudent(ScCourseStudent scCourseStudent);

    int updateScCourseStudent(ScCourseStudent scCourseStudent);

    int deleteScCourseStudentByIds(Long[] ids);

    int deleteScCourseStudentById(Long id);
}
