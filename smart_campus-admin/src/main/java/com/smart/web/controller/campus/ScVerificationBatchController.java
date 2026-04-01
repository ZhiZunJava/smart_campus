package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.smart.common.utils.SecurityUtils;
import com.smart.system.domain.ScVerificationBatch;
import com.smart.system.domain.ScVerificationRecord;
import com.smart.system.domain.dto.VerificationBatchCreateDto;
import com.smart.system.domain.dto.VerificationBatchModifyDto;
import com.smart.system.domain.dto.VerificationBatchReviewDto;
import com.smart.system.domain.dto.VerificationReviewDto;
import com.smart.system.service.IScVerificationBatchService;

/**
 * 学籍核对批次管理 Controller
 *
 * @author can
 */
@RestController
@RequestMapping("/campus/verification")
public class ScVerificationBatchController extends BaseController {

    @Autowired
    private IScVerificationBatchService verificationService;

    /**
     * 分页查询批次列表
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:list')")
    @GetMapping("/batch/list")
    public TableDataInfo list(ScVerificationBatch query) {
        startPage();
        List<ScVerificationBatch> list = verificationService.selectBatchList(query);
        return getDataTable(list);
    }

    /**
     * 批次详情（含统计）
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:query')")
    @GetMapping("/batch/{batchId}")
    public AjaxResult getInfo(@PathVariable Long batchId) {
        return success(verificationService.selectBatchDetail(batchId));
    }

    /**
     * 创建批次
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:add')")
    @Log(title = "学籍核对批次", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult add(@Validated @RequestBody VerificationBatchCreateDto dto) {
        return toAjax(verificationService.createBatch(dto, getUsername()));
    }

    /**
     * 修改批次
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:edit')")
    @Log(title = "学籍核对批次", businessType = BusinessType.UPDATE)
    @PutMapping("/batch")
    public AjaxResult edit(@Validated @RequestBody VerificationBatchCreateDto dto) {
        return toAjax(verificationService.updateBatch(dto, getUsername()));
    }

    /**
     * 激活批次
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:edit')")
    @Log(title = "学籍核对批次激活", businessType = BusinessType.UPDATE)
    @PutMapping("/batch/activate/{batchId}")
    public AjaxResult activate(@PathVariable Long batchId) {
        return toAjax(verificationService.activateBatch(batchId, getUsername()));
    }

    /**
     * 关闭批次
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:edit')")
    @Log(title = "学籍核对批次关闭", businessType = BusinessType.UPDATE)
    @PutMapping("/batch/close/{batchId}")
    public AjaxResult close(@PathVariable Long batchId) {
        return toAjax(verificationService.closeBatch(batchId, getUsername()));
    }

    /**
     * 删除批次
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:remove')")
    @Log(title = "学籍核对批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds) {
        return toAjax(verificationService.deleteBatchByIds(batchIds));
    }

    /**
     * 查询某批次下的学生记录列表
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:list')")
    @GetMapping("/record/list")
    public TableDataInfo recordList(ScVerificationRecord query) {
        startPage();
        List<ScVerificationRecord> list = verificationService.selectRecordList(query);
        return getDataTable(list);
    }

    /**
     * 查看记录详情（含变更明细）
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:query')")
    @GetMapping("/record/{recordId}")
    public AjaxResult recordDetail(@PathVariable Long recordId) {
        return success(verificationService.selectRecordDetail(recordId));
    }

    /**
     * 审核单条记录
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:review')")
    @Log(title = "学籍核对审核", businessType = BusinessType.UPDATE)
    @PutMapping("/record/review")
    public AjaxResult review(@Validated @RequestBody VerificationReviewDto dto) {
        return toAjax(verificationService.reviewRecord(dto, getUsername()));
    }

    /**
     * 批量审核
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:review')")
    @Log(title = "学籍核对批量审核", businessType = BusinessType.UPDATE)
    @PutMapping("/record/review/batch")
    public AjaxResult batchReview(@Validated @RequestBody VerificationBatchReviewDto dto) {
        return toAjax(verificationService.batchReviewRecords(dto, getUsername()));
    }

    /**
     * 管理员批量直接修改
     */
    @PreAuthorize("@ss.hasPermi('campus:verification:edit')")
    @Log(title = "学籍核对批量修改", businessType = BusinessType.UPDATE)
    @PutMapping("/record/batch-modify")
    public AjaxResult batchModify(@Validated @RequestBody VerificationBatchModifyDto dto) {
        return toAjax(verificationService.adminBatchModify(dto, getUsername()));
    }
}
