package com.smart.web.controller.campus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.system.service.ICampusDetailService;

/**
 * 校园详情聚合接口
 *
 * @author can
 */
@RestController
@RequestMapping("/campus/detail")
public class CampusDetailController extends BaseController {
    @Autowired
    private ICampusDetailService campusDetailService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/resource/{resourceId}")
    public AjaxResult resourceDetail(@PathVariable Long resourceId) {
        return success(campusDetailService.getResourceDetail(resourceId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/question/{questionId}")
    public AjaxResult questionDetail(@PathVariable Long questionId) {
        return success(campusDetailService.getQuestionDetail(questionId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/paper/{paperId}")
    public AjaxResult paperDetail(@PathVariable Long paperId) {
        return success(campusDetailService.getExamPaperDetail(paperId));
    }

    @GetMapping("/qa/session/{sessionId}")
    public AjaxResult qaSessionDetail(@PathVariable Long sessionId) {
        return success(campusDetailService.getQaSessionDetail(sessionId));
    }
}
