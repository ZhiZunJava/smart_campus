package com.smart.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.smart.system.domain.ScCourseStudent;

public interface ScCourseStudentMapper {
    ScCourseStudent selectScCourseStudentById(Long id);

    List<ScCourseStudent> selectScCourseStudentList(ScCourseStudent scCourseStudent);

    ScCourseStudent selectDuplicateScCourseStudent(ScCourseStudent scCourseStudent);

    ScCourseStudent selectAnyByStudentAndClassCourse(Long studentUserId, Long classCourseId);

    int countActiveByClassCourseId(Long classCourseId);

    /** 带行锁的容量计数，用于事务内并发安全校验 */
    int countActiveByClassCourseIdForUpdate(Long classCourseId);

    List<Map<String, Object>> selectSelectionCountSnapshots(@Param("classCourseIds") Long[] classCourseIds);

    int insertScCourseStudent(ScCourseStudent scCourseStudent);

    int updateScCourseStudent(ScCourseStudent scCourseStudent);

    int deleteScCourseStudentById(Long id);

    int deleteScCourseStudentByIds(Long[] ids);
}
