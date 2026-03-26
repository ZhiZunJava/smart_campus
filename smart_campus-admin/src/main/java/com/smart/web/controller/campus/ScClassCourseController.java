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
import com.smart.system.domain.ScClassCourse;
import com.smart.system.service.IScClassCourseService;
import com.smart.system.service.impl.TeachingCodeRuleService;

@RestController
@RequestMapping("/campus/classCourse")
public class ScClassCourseController extends BaseController {
    @Autowired
    private IScClassCourseService scClassCourseService;
    @Autowired
    private TeachingCodeRuleService teachingCodeRuleService;

    @PreAuthorize("@ss.hasPermi('campus:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScClassCourse scClassCourse) {
        startPage();
        List<ScClassCourse> list = scClassCourseService.selectScClassCourseList(scClassCourse);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:course:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(scClassCourseService.selectScClassCourseById(id));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @PostMapping("/generate-teaching-code")
    public AjaxResult generateTeachingCode(@RequestBody ScClassCourse scClassCourse) {
        return success(teachingCodeRuleService.generateTeachingClassCode(scClassCourse));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @PostMapping("/generate-selection-group-code")
    public AjaxResult generateSelectionGroupCode(@RequestBody ScClassCourse scClassCourse) {
        return success(teachingCodeRuleService.generateSelectionGroupCode(scClassCourse));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:add')")
    @Log(title = "班级课程", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScClassCourse scClassCourse) {
        scClassCourse.setCreateBy(getUsername());
        return toAjax(scClassCourseService.insertScClassCourse(scClassCourse));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @Log(title = "班级课程", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScClassCourse scClassCourse) {
        scClassCourse.setUpdateBy(getUsername());
        return toAjax(scClassCourseService.updateScClassCourse(scClassCourse));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:remove')")
    @Log(title = "班级课程", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(scClassCourseService.deleteScClassCourseByIds(ids));
    }
}
