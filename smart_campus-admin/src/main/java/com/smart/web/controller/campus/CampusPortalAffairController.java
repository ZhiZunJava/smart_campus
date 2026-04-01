package com.smart.web.controller.campus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.system.domain.dto.AffairBatchReviewDto;
import com.smart.system.domain.dto.AffairRequestReviewDto;
import com.smart.system.domain.dto.AffairRequestSubmitDto;
import com.smart.system.service.IScAffairRequestService;
import com.smart.system.service.IScAffairWorkStudyJobService;

@RestController
@RequestMapping("/campus/portal")
public class CampusPortalAffairController extends BaseController {
    @Autowired
    private IScAffairRequestService scAffairRequestService;
    @Autowired
    private IScAffairWorkStudyJobService scAffairWorkStudyJobService;

    // ===== 通用端点 =====

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{role}/affair/statistics")
    public AjaxResult statistics(@PathVariable String role) {
        return success(scAffairRequestService.buildAffairStatistics(getLoginUser(), role));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{role}/affair/category/{categoryCode}")
    public AjaxResult categoryDetail(@PathVariable String role, @PathVariable String categoryCode) {
        return success(scAffairRequestService.buildCategoryDetail(getLoginUser(), role, categoryCode));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{role}/affair/recent-activity")
    public AjaxResult recentActivity(@PathVariable String role) {
        return success(scAffairRequestService.listRecentActivity(getLoginUser(), role, 10));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{role}/affair/frequent-templates")
    public AjaxResult frequentTemplates(@PathVariable String role) {
        return success(scAffairRequestService.listFrequentTemplates(getLoginUser(), role, 6));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/affair/options")
    public AjaxResult options() {
        return success(scAffairRequestService.buildPortalOptions());
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/affair/services")
    public AjaxResult studentServices() {
        return success(scAffairRequestService.buildServiceCatalog("student", getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/affair/dashboard")
    public AjaxResult studentDashboard() {
        return success(scAffairRequestService.buildPortalDashboard(getLoginUser(), "student"));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/affair/request/list")
    public AjaxResult studentRequestList() {
        return success(scAffairRequestService.listMyRequests(getLoginUser(), "student"));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/affair/request/{requestId}")
    public AjaxResult studentRequestDetail(@PathVariable Long requestId) {
        return success(scAffairRequestService.selectRequestDetail(requestId, getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/student/affair/work-study/jobs")
    public AjaxResult studentWorkStudyJobs() {
        return success(scAffairWorkStudyJobService.selectOpenWorkStudyJobs());
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/student/affair/request")
    public AjaxResult studentSubmit(@Validated @RequestBody AffairRequestSubmitDto dto) {
        return success(scAffairRequestService.submitRequest(dto, getLoginUser(), "student"));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PutMapping("/student/affair/request/{requestId}/cancel")
    public AjaxResult studentCancel(@PathVariable Long requestId) {
        return success(scAffairRequestService.cancelRequest(requestId, getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/affair/services")
    public AjaxResult teacherServices() {
        return success(scAffairRequestService.buildServiceCatalog("teacher", getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/affair/dashboard")
    public AjaxResult teacherDashboard() {
        return success(scAffairRequestService.buildPortalDashboard(getLoginUser(), "teacher"));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/affair/request/list")
    public AjaxResult teacherRequestList() {
        return success(scAffairRequestService.listMyRequests(getLoginUser(), "teacher"));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/affair/request/{requestId}")
    public AjaxResult teacherRequestDetail(@PathVariable Long requestId) {
        return success(scAffairRequestService.selectRequestDetail(requestId, getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @PostMapping("/teacher/affair/request")
    public AjaxResult teacherSubmit(@Validated @RequestBody AffairRequestSubmitDto dto) {
        return success(scAffairRequestService.submitRequest(dto, getLoginUser(), "teacher"));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @PutMapping("/teacher/affair/request/{requestId}/cancel")
    public AjaxResult teacherCancel(@PathVariable Long requestId) {
        return success(scAffairRequestService.cancelRequest(requestId, getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @GetMapping("/teacher/affair/review/list")
    public AjaxResult teacherReviewList() {
        return success(scAffairRequestService.listPendingReviews(getLoginUser(), "teacher"));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @PutMapping("/teacher/affair/review")
    public AjaxResult teacherReview(@Validated @RequestBody AffairRequestReviewDto dto) {
        return success(scAffairRequestService.reviewRequest(dto, getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,admin')")
    @PutMapping("/teacher/affair/review/batch")
    public AjaxResult teacherBatchReview(@Validated @RequestBody AffairBatchReviewDto dto) {
        return success(scAffairRequestService.batchReviewRequests(dto, getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('advisor,head_teacher,campus_head_teacher,admin')")
    @GetMapping("/advisor/affair/dashboard")
    public AjaxResult advisorDashboard() {
        return success(scAffairRequestService.buildPortalDashboard(getLoginUser(), "advisor"));
    }

    @PreAuthorize("@ss.hasAnyRoles('advisor,head_teacher,campus_head_teacher,admin')")
    @GetMapping("/advisor/affair/review/list")
    public AjaxResult advisorReviewList() {
        return success(scAffairRequestService.listPendingReviews(getLoginUser(), "advisor"));
    }

    @PreAuthorize("@ss.hasAnyRoles('advisor,head_teacher,campus_head_teacher,admin')")
    @GetMapping("/advisor/affair/request/{requestId}")
    public AjaxResult advisorRequestDetail(@PathVariable Long requestId) {
        return success(scAffairRequestService.selectRequestDetail(requestId, getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('advisor,head_teacher,campus_head_teacher,admin')")
    @PutMapping("/advisor/affair/review")
    public AjaxResult advisorReview(@Validated @RequestBody AffairRequestReviewDto dto) {
        return success(scAffairRequestService.reviewRequest(dto, getLoginUser()));
    }

    @PreAuthorize("@ss.hasAnyRoles('advisor,head_teacher,campus_head_teacher,admin')")
    @PutMapping("/advisor/affair/review/batch")
    public AjaxResult advisorBatchReview(@Validated @RequestBody AffairBatchReviewDto dto) {
        return success(scAffairRequestService.batchReviewRequests(dto, getLoginUser()));
    }
}
