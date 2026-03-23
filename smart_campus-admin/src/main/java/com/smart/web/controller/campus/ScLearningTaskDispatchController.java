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
import com.smart.system.domain.ScLearningTaskDispatch;
import com.smart.system.service.IScLearningTaskDispatchService;

@RestController
@RequestMapping("/campus/learningTaskDispatch")
public class ScLearningTaskDispatchController extends BaseController {
    @Autowired
    private IScLearningTaskDispatchService scLearningTaskDispatchService;

    @PreAuthorize("@ss.hasPermi('campus:learningTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScLearningTaskDispatch scLearningTaskDispatch) {
        startPage();
        List<ScLearningTaskDispatch> list = scLearningTaskDispatchService.selectScLearningTaskDispatchList(scLearningTaskDispatch);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:query')")
    @GetMapping("/{dispatchId}")
    public AjaxResult getInfo(@PathVariable Long dispatchId) {
        return success(scLearningTaskDispatchService.selectScLearningTaskDispatchByDispatchId(dispatchId));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:edit')")
    @Log(title = "学习任务派发", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScLearningTaskDispatch scLearningTaskDispatch) {
        scLearningTaskDispatch.setUpdateBy(getUsername());
        return toAjax(scLearningTaskDispatchService.updateScLearningTaskDispatch(scLearningTaskDispatch));
    }
}
