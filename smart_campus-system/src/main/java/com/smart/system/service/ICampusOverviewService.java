package com.smart.system.service;

import com.smart.system.domain.campusvo.CampusDashboardVo;
import com.smart.system.domain.campusvo.ParentDashboardVo;
import com.smart.system.domain.campusvo.StudentDashboardVo;
import com.smart.system.domain.campusvo.TeacherDashboardVo;

/**
 * 校园概览聚合服务
 *
 * @author can
 */
public interface ICampusOverviewService {
    CampusDashboardVo getDashboard(Long userId, Long courseId, Integer recommendLimit);

    StudentDashboardVo getStudentDashboard(Long userId, Long courseId, Integer recommendLimit);

    TeacherDashboardVo getTeacherDashboard(Long teacherId);

    ParentDashboardVo getParentDashboard(Long parentUserId, Long studentUserId, Long courseId);
}
