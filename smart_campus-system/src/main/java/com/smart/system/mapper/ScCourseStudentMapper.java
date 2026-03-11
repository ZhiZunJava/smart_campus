package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScCourseStudent;

public interface ScCourseStudentMapper
{
    ScCourseStudent selectScCourseStudentById(Long id);
    List<ScCourseStudent> selectScCourseStudentList(ScCourseStudent scCourseStudent);
    int insertScCourseStudent(ScCourseStudent scCourseStudent);
    int updateScCourseStudent(ScCourseStudent scCourseStudent);
    int deleteScCourseStudentById(Long id);
    int deleteScCourseStudentByIds(Long[] ids);
}
