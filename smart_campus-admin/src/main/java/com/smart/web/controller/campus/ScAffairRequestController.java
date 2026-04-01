package com.smart.web.controller.campus;

import java.util.List;
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
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.common.utils.poi.ExcelUtil;
import com.smart.system.domain.ScAffairRequest;
import com.smart.system.domain.dto.AffairBatchReviewDto;
import com.smart.system.domain.dto.AffairRequestReviewDto;
import com.smart.system.service.IScAffairRequestService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/campus/affair/request")
public class ScAffairRequestController extends BaseController {
    @Autowired
    private IScAffairRequestService scAffairRequestService;

    @PreAuthorize("@ss.hasPermi('campus:affairRequest:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScAffairRequest scAffairRequest) {
        startPage();
        List<ScAffairRequest> list = scAffairRequestService.selectScAffairRequestList(scAffairRequest);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:affairRequest:query')")
    @GetMapping("/{requestId}")
    public AjaxResult getInfo(@PathVariable Long requestId) {
        return success(scAffairRequestService.selectRequestDetail(requestId, getLoginUser()));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairRequest:review')")
    @Log(title = "学生事务申请审核", businessType = BusinessType.UPDATE)
    @PutMapping("/review")
    public AjaxResult review(@Validated @RequestBody AffairRequestReviewDto dto) {
        return success(scAffairRequestService.reviewRequest(dto, getLoginUser()));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairRequest:review')")
    @Log(title = "学生事务批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/review/batch")
    public AjaxResult batchReview(@Validated @RequestBody AffairBatchReviewDto dto) {
        return success(scAffairRequestService.batchReviewRequests(dto, getLoginUser()));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairRequest:list')")
    @Log(title = "学生事务申请导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody(required = false) ScAffairRequest scAffairRequest) {
        List<ScAffairRequest> list = scAffairRequestService.exportRequestList(scAffairRequest);
        ExcelUtil<ScAffairRequest> util = new ExcelUtil<>(ScAffairRequest.class);
        util.exportExcel(response, list, "学生事务申请数据");
    }

    @PreAuthorize("@ss.hasPermi('campus:affairRequest:query')")
    @GetMapping("/statistics")
    public AjaxResult statistics() {
        return success(scAffairRequestService.buildAffairStatistics(getLoginUser(), "admin"));
    }
}
