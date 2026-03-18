package com.smart.web.controller.campus;

import java.util.Map;
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
import com.smart.system.domain.ScCourseSchedule;
import com.smart.system.domain.dto.CourseScheduleAutoArrangeDto;
import com.smart.system.service.IScCourseScheduleService;

@RestController
@RequestMapping("/campus/courseSchedule")
public class ScCourseScheduleController extends BaseController {
    @Autowired
    private IScCourseScheduleService scCourseScheduleService;

    @PreAuthorize("@ss.hasPermi('campus:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScCourseSchedule scCourseSchedule) {
        startPage();
        List<ScCourseSchedule> list = scCourseScheduleService.selectScCourseScheduleList(scCourseSchedule);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:course:query')")
    @GetMapping("/{scheduleId}")
    public AjaxResult getInfo(@PathVariable Long scheduleId) {
        return success(scCourseScheduleService.selectScCourseScheduleByScheduleId(scheduleId));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:query')")
    @PostMapping("/conflict-check")
    public AjaxResult conflictCheck(@RequestBody ScCourseSchedule scCourseSchedule) {
        Map<String, Object> result = scCourseScheduleService.checkScheduleConflict(scCourseSchedule);
        return success(result);
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @Log(title = "排课表", businessType = BusinessType.OTHER)
    @PostMapping("/auto-arrange")
    public AjaxResult autoArrange(@RequestBody CourseScheduleAutoArrangeDto request) {
        return success(scCourseScheduleService.autoArrangeCourseSchedule(request, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:add')")
    @Log(title = "排课表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScCourseSchedule scCourseSchedule) {
        scCourseSchedule.setCreateBy(getUsername());
        return toAjax(scCourseScheduleService.insertScCourseSchedule(scCourseSchedule));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:edit')")
    @Log(title = "排课表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScCourseSchedule scCourseSchedule) {
        scCourseSchedule.setUpdateBy(getUsername());
        return toAjax(scCourseScheduleService.updateScCourseSchedule(scCourseSchedule));
    }

    @PreAuthorize("@ss.hasPermi('campus:course:remove')")
    @Log(title = "排课表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scheduleIds}")
    public AjaxResult remove(@PathVariable Long[] scheduleIds) {
        return toAjax(scCourseScheduleService.deleteScCourseScheduleByScheduleIds(scheduleIds));
    }
}
