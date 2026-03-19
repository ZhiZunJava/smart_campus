package com.smart.web.controller.campus;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.system.domain.ScCourseStudent;
import com.smart.system.domain.dto.CourseStudentBatchAddDto;
import com.smart.system.service.IScCourseStudentService;

@RestController
@RequestMapping("/campus/courseStudent")
public class ScCourseStudentController extends BaseController {
    @Autowired
    private IScCourseStudentService scCourseStudentService;

    @PreAuthorize("@ss.hasPermi('campus:courseStudent:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScCourseStudent scCourseStudent) {
        startPage();
        List<ScCourseStudent> list = scCourseStudentService.selectScCourseStudentList(scCourseStudent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:courseStudent:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(scCourseStudentService.selectScCourseStudentById(id));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseStudent:query')")
    @PostMapping("/duplicate-check")
    public AjaxResult duplicateCheck(@RequestBody ScCourseStudent scCourseStudent) {
        Map<String, Object> result = scCourseStudentService.checkDuplicate(scCourseStudent);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('campus:courseStudent:add')")
    @Log(title = "课程学生关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScCourseStudent scCourseStudent) {
        scCourseStudent.setCreateBy(getUsername());
        return toAjax(scCourseStudentService.insertScCourseStudent(scCourseStudent));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseStudent:add')")
    @Log(title = "课程学生关系", businessType = BusinessType.INSERT)
    @PostMapping("/batch-add")
    public AjaxResult batchAdd(@RequestBody CourseStudentBatchAddDto batchDto) {
        return success(scCourseStudentService.batchAddCourseStudents(batchDto));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseStudent:edit')")
    @Log(title = "课程学生关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScCourseStudent scCourseStudent) {
        scCourseStudent.setUpdateBy(getUsername());
        return toAjax(scCourseStudentService.updateScCourseStudent(scCourseStudent));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseStudent:remove')")
    @Log(title = "课程学生关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(scCourseStudentService.deleteScCourseStudentByIds(ids));
    }
}
