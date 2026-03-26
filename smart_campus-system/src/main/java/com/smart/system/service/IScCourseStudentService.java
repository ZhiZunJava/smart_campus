package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScClassCourse;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.dto.CourseStudentBatchAddDto;

public interface IScCourseStudentService {
    ScCourseStudent selectScCourseStudentById(Long id);

    List<ScCourseStudent> selectScCourseStudentList(ScCourseStudent scCourseStudent);

    Map<String, Object> checkDuplicate(ScCourseStudent scCourseStudent);

    Map<String, Object> batchAddCourseStudents(CourseStudentBatchAddDto batchDto);

    int countActiveByClassCourseId(Long classCourseId);

    ScCourseStudent findActiveSelection(Long studentUserId, Long classCourseId);

    List<ScCourseStudent> listStudentCourseSelections(Long studentUserId, Long termId, boolean autoFillRequired);

    List<ScClassCourse> listAvailableClassCoursesForStudent(Long studentUserId, Long termId);

    Map<String, Object> selectCourse(Long studentUserId, Long classCourseId, String operator);

    Map<String, Object> cancelCourse(Long studentUserId, Long classCourseId, String operator);

    Map<String, Object> selectCourseByApproval(Long studentUserId, Long classCourseId, String operator);

    Map<String, Object> cancelCourseByApproval(Long studentUserId, Long classCourseId, String operator);

    List<ScCourseStudent> listTeacherCourseStudents(Long teacherId, Long classCourseId, Long termId);

    int insertScCourseStudent(ScCourseStudent scCourseStudent);

    int updateScCourseStudent(ScCourseStudent scCourseStudent);

    int deleteScCourseStudentByIds(Long[] ids);

    int deleteScCourseStudentById(Long id);
}
