package com.smart.web.controller.campus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.system.service.ICampusOverviewService;

/**
 * 学生 / 教师 / 家长端聚合接口
 *
 * @author Codex
 */
@RestController
@RequestMapping("/campus/portal")
public class CampusPortalController extends BaseController
{
    @Autowired
    private ICampusOverviewService campusOverviewService;

    @PreAuthorize("@ss.hasPermi('campus:overview:query')")
    @GetMapping("/student/dashboard")
    public AjaxResult studentDashboard(@RequestParam Long userId,
                                       @RequestParam(required = false) Long courseId,
                                       @RequestParam(required = false) Integer recommendLimit)
    {
        return success(campusOverviewService.getStudentDashboard(userId, courseId, recommendLimit));
    }

    @PreAuthorize("@ss.hasPermi('campus:overview:query')")
    @GetMapping("/teacher/dashboard")
    public AjaxResult teacherDashboard(@RequestParam Long teacherId)
    {
        return success(campusOverviewService.getTeacherDashboard(teacherId));
    }

    @PreAuthorize("@ss.hasPermi('campus:overview:query')")
    @GetMapping("/parent/dashboard")
    public AjaxResult parentDashboard(@RequestParam Long parentUserId,
                                      @RequestParam(required = false) Long studentUserId,
                                      @RequestParam(required = false) Long courseId)
    {
        return success(campusOverviewService.getParentDashboard(parentUserId, studentUserId, courseId));
    }
}
