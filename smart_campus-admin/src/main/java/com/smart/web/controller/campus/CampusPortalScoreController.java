package com.smart.web.controller.campus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.system.service.IScStudentScoreService;

@RestController
@RequestMapping("/campus/portal/score")
public class CampusPortalScoreController extends BaseController {
    @Autowired
    private IScStudentScoreService scStudentScoreService;

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/list")
    public AjaxResult list(@RequestParam Long userId,
            @RequestParam(required = false) Long termId,
            @RequestParam(required = false) Long courseId) {
        return success(scStudentScoreService.selectPortalStudentScoreList(userId, termId, courseId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/overview")
    public AjaxResult overview(@RequestParam Long userId,
            @RequestParam(required = false) Long termId) {
        return success(scStudentScoreService.buildPortalScoreOverview(userId, termId));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/detail")
    public AjaxResult detail(@RequestParam Long userId, @RequestParam Long classCourseId) {
        return success(scStudentScoreService.buildPortalScoreDetail(userId, classCourseId));
    }
}
