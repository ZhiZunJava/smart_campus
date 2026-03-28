package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.system.domain.ScCourseSelectionRequest;
import com.smart.system.domain.dto.CourseSelectionRequestReviewDto;
import com.smart.system.service.IScCourseSelectionRequestService;

@RestController
@RequestMapping("/campus/courseSelectionRequest")
public class ScCourseSelectionRequestController extends BaseController {
    @Autowired
    private IScCourseSelectionRequestService scCourseSelectionRequestService;

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionRequest:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScCourseSelectionRequest request) {
        startPage();
        List<ScCourseSelectionRequest> list = scCourseSelectionRequestService.selectScCourseSelectionRequestList(request);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionRequest:query')")
    @GetMapping("/{requestId}")
    public AjaxResult getInfo(@PathVariable Long requestId) {
        return success(scCourseSelectionRequestService.selectScCourseSelectionRequestByRequestId(requestId));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionRequest:review')")
    @Log(title = "个性化选课申请", businessType = BusinessType.UPDATE)
    @PutMapping("/review")
    public AjaxResult review(@Validated @RequestBody CourseSelectionRequestReviewDto dto) {
        return success(scCourseSelectionRequestService.reviewRequest(dto, getUsername(), getUserId()));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionRequest:review')")
    @Log(title = "批量审核选课申请", businessType = BusinessType.UPDATE)
    @PutMapping("/batchReview")
    public AjaxResult batchReview(@Validated @RequestBody List<CourseSelectionRequestReviewDto> dtos) {
        int successCount = 0;
        for (CourseSelectionRequestReviewDto dto : dtos) {
            scCourseSelectionRequestService.reviewRequest(dto, getUsername(), getUserId());
            successCount++;
        }
        return success("成功审核 " + successCount + " 条申请");
    }

}
