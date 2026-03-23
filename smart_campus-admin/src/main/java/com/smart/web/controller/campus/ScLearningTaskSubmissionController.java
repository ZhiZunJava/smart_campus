package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.system.domain.ScLearningTaskSubmission;
import com.smart.system.service.IScLearningTaskSubmissionService;

@RestController
@RequestMapping("/campus/learningTaskSubmission")
public class ScLearningTaskSubmissionController extends BaseController {
    @Autowired
    private IScLearningTaskSubmissionService scLearningTaskSubmissionService;

    @PreAuthorize("@ss.hasPermi('campus:learningTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScLearningTaskSubmission scLearningTaskSubmission) {
        startPage();
        List<ScLearningTaskSubmission> list = scLearningTaskSubmissionService.selectScLearningTaskSubmissionList(scLearningTaskSubmission);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:query')")
    @GetMapping("/{submissionId}")
    public AjaxResult getInfo(@PathVariable Long submissionId) {
        return success(scLearningTaskSubmissionService.selectScLearningTaskSubmissionBySubmissionId(submissionId));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:edit')")
    @Log(title = "学习任务提交", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScLearningTaskSubmission scLearningTaskSubmission) {
        scLearningTaskSubmission.setUpdateBy(getUsername());
        return toAjax(scLearningTaskSubmissionService.updateScLearningTaskSubmission(scLearningTaskSubmission));
    }
}
