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
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.utils.SecurityUtils;
import com.smart.system.domain.ScVerificationBatch;
import com.smart.system.domain.ScVerificationRecord;
import com.smart.system.domain.dto.VerificationSubmitDto;
import com.smart.system.service.IScVerificationBatchService;
import com.smart.system.service.IScUserProfileService;

/**
 * 学生端学籍核对 Controller
 *
 * @author can
 */
@RestController
@RequestMapping("/campus/portal/student/verification")
public class CampusPortalVerificationController extends BaseController {

    @Autowired
    private IScVerificationBatchService verificationService;

    @Autowired
    private IScUserProfileService userProfileService;

    /**
     * 获取学生可参与的活跃批次列表
     */
    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/batches")
    public AjaxResult activeBatches() {
        Long userId = SecurityUtils.getUserId();
        List<ScVerificationBatch> batches = verificationService.selectActiveBatchesForStudent(userId);
        return success(batches);
    }

    /**
     * 获取学生在某批次下的核对信息（当前档案+可编辑字段+已提交变更）
     */
    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/info/{batchId}")
    public AjaxResult verificationInfo(@PathVariable Long batchId) {
        Long userId = SecurityUtils.getUserId();
        ScVerificationRecord record = verificationService.getStudentVerificationInfo(batchId, userId);

        // 同时返回学生当前档案信息
        AjaxResult ajax = success(record);
        ajax.put("profile", userProfileService.selectScUserProfileByUserId(userId));
        ajax.put("batch", verificationService.selectBatchDetail(batchId));
        return ajax;
    }

    /**
     * 学生确认无需修改
     */
    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PutMapping("/confirm/{recordId}")
    public AjaxResult confirm(@PathVariable Long recordId) {
        Long userId = SecurityUtils.getUserId();
        return toAjax(verificationService.confirmNoChange(recordId, userId));
    }

    /**
     * 学生提交变更
     */
    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @PostMapping("/submit")
    public AjaxResult submit(@Validated @RequestBody VerificationSubmitDto dto) {
        Long userId = SecurityUtils.getUserId();
        return toAjax(verificationService.submitChanges(dto, userId));
    }

    /**
     * 查看学生自己的所有核对记录
     */
    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/my-records")
    public AjaxResult myRecords() {
        Long userId = SecurityUtils.getUserId();
        List<ScVerificationRecord> records = verificationService.selectStudentRecords(userId);
        return success(records);
    }

    /**
     * 查看单条记录详情
     */
    @PreAuthorize("@ss.hasAnyRoles('student,admin')")
    @GetMapping("/record/{recordId}")
    public AjaxResult recordDetail(@PathVariable Long recordId) {
        return success(verificationService.selectRecordDetail(recordId));
    }
}
