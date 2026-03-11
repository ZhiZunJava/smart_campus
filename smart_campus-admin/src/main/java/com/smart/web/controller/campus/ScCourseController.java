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
import com.smart.system.domain.ScCourse;
import com.smart.system.service.IScCourseService;

@RestController
@RequestMapping("/campus/course")
public class ScCourseController extends BaseController
{
    @Autowired
    private IScCourseService scCourseService;

    @PreAuthorize("@ss.hasPermi('campus:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScCourse scCourse)
    {
        startPage();
        List<ScCourse> list = scCourseService.selectScCourseList(scCourse);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:course:query')")
    @GetMapping(value = "/{courseId}")
    public AjaxResult getInfo(@PathVariable Long courseId)
    {
        return success(scCourseService.selectScCourseByCourseId(courseId));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:add')")
    @Log(title = "课程管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScCourse scCourse)
    {
        scCourse.setCreateBy(getUsername());
        return toAjax(scCourseService.insertScCourse(scCourse));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @Log(title = "课程管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScCourse scCourse)
    {
        scCourse.setUpdateBy(getUsername());
        return toAjax(scCourseService.updateScCourse(scCourse));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:remove')")
    @Log(title = "课程管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{courseIds}")
    public AjaxResult remove(@PathVariable Long[] courseIds)
    {
        return toAjax(scCourseService.deleteScCourseByCourseIds(courseIds));
    }
}
