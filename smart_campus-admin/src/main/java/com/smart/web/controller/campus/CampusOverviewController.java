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
 * 校园首页聚合接口
 *
 * @author can
 */
@RestController
@RequestMapping("/campus/overview")
public class CampusOverviewController extends BaseController {
    @Autowired
    private ICampusOverviewService campusOverviewService;

    @PreAuthorize("@ss.hasPermi('campus:overview:query')")
    @GetMapping("/dashboard")
    public AjaxResult dashboard(@RequestParam Long userId,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer recommendLimit) {
        return success(campusOverviewService.getDashboard(userId, courseId, recommendLimit));
    }
}
