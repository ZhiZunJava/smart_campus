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
import com.smart.system.domain.ScLearningTask;
import com.smart.system.service.IScLearningTaskService;

@RestController
@RequestMapping("/campus/learningTask")
public class ScLearningTaskController extends BaseController {
    @Autowired
    private IScLearningTaskService scLearningTaskService;

    @PreAuthorize("@ss.hasPermi('campus:learningTask:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScLearningTask scLearningTask) {
        startPage();
        List<ScLearningTask> list = scLearningTaskService.selectScLearningTaskList(scLearningTask);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:query')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId) {
        return success(scLearningTaskService.selectScLearningTaskByTaskId(taskId));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:add')")
    @Log(title = "学习任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScLearningTask scLearningTask) {
        scLearningTask.setCreateBy(getUsername());
        int rows = scLearningTaskService.insertScLearningTask(scLearningTask);
        if (rows <= 0) {
            return error("新增学习任务失败");
        }
        return success().put("taskId", scLearningTask.getTaskId());
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:edit')")
    @Log(title = "学习任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScLearningTask scLearningTask) {
        scLearningTask.setUpdateBy(getUsername());
        int rows = scLearningTaskService.updateScLearningTask(scLearningTask);
        if (rows <= 0) {
            return error("修改学习任务失败");
        }
        return success().put("taskId", scLearningTask.getTaskId());
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:dispatch')")
    @Log(title = "学习任务派发", businessType = BusinessType.OTHER)
    @PostMapping("/{taskId}/dispatch")
    public AjaxResult dispatch(@PathVariable Long taskId, @RequestBody Long[] userIds) {
        return toAjax(scLearningTaskService.dispatchTask(taskId, userIds, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:dispatch')")
    @Log(title = "学习任务按班级派发", businessType = BusinessType.OTHER)
    @PostMapping("/{taskId}/dispatch/class/{classId}")
    public AjaxResult dispatchToClass(@PathVariable Long taskId, @PathVariable Long classId) {
        return toAjax(scLearningTaskService.dispatchTaskToClass(taskId, classId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:dispatch')")
    @Log(title = "学习任务按课程派发", businessType = BusinessType.OTHER)
    @PostMapping("/{taskId}/dispatch/course/{courseId}")
    public AjaxResult dispatchToCourse(@PathVariable Long taskId, @PathVariable Long courseId) {
        return toAjax(scLearningTaskService.dispatchTaskToCourse(taskId, courseId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningTask:remove')")
    @Log(title = "学习任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds) {
        return toAjax(scLearningTaskService.deleteScLearningTaskByTaskIds(taskIds));
    }
}
