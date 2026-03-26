package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.smart.system.domain.ScCourseSelectionPlan;
import com.smart.system.service.IScCourseSelectionPlanService;

@RestController
@RequestMapping("/campus/courseSelectionPlan")
public class ScCourseSelectionPlanController extends BaseController {
    @Autowired
    private IScCourseSelectionPlanService scCourseSelectionPlanService;

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionPlan:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScCourseSelectionPlan plan) {
        startPage();
        List<ScCourseSelectionPlan> list = scCourseSelectionPlanService.selectScCourseSelectionPlanList(plan);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionPlan:query')")
    @GetMapping("/{planId}")
    public AjaxResult getInfo(@PathVariable Long planId) {
        return success(scCourseSelectionPlanService.selectScCourseSelectionPlanByPlanId(planId));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionPlan:add')")
    @Log(title = "选课计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ScCourseSelectionPlan plan) {
        plan.setCreateBy(getUsername());
        return toAjax(scCourseSelectionPlanService.insertScCourseSelectionPlan(plan));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionPlan:edit')")
    @Log(title = "选课计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ScCourseSelectionPlan plan) {
        plan.setUpdateBy(getUsername());
        return toAjax(scCourseSelectionPlanService.updateScCourseSelectionPlan(plan));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseSelectionPlan:remove')")
    @Log(title = "选课计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{planIds}")
    public AjaxResult remove(@PathVariable Long[] planIds) {
        return toAjax(scCourseSelectionPlanService.deleteScCourseSelectionPlanByPlanIds(planIds));
    }
}
