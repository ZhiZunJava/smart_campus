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
import com.smart.system.domain.ScCourseChapter;
import com.smart.system.service.IScCourseChapterService;

@RestController
@RequestMapping("/campus/courseChapter")
public class ScCourseChapterController extends BaseController {
    @Autowired
    private IScCourseChapterService scCourseChapterService;

    @PreAuthorize("@ss.hasPermi('campus:courseChapter:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScCourseChapter scCourseChapter) {
        startPage();
        List<ScCourseChapter> list = scCourseChapterService.selectScCourseChapterList(scCourseChapter);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:courseChapter:query')")
    @GetMapping(value = "/{chapterId}")
    public AjaxResult getInfo(@PathVariable Long chapterId) {
        return success(scCourseChapterService.selectScCourseChapterByChapterId(chapterId));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseChapter:add')")
    @Log(title = "课程章节", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScCourseChapter scCourseChapter) {
        scCourseChapter.setCreateBy(getUsername());
        return toAjax(scCourseChapterService.insertScCourseChapter(scCourseChapter));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseChapter:edit')")
    @Log(title = "课程章节", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScCourseChapter scCourseChapter) {
        scCourseChapter.setUpdateBy(getUsername());
        return toAjax(scCourseChapterService.updateScCourseChapter(scCourseChapter));
    }

    @PreAuthorize("@ss.hasPermi('campus:courseChapter:remove')")
    @Log(title = "课程章节", businessType = BusinessType.DELETE)
    @DeleteMapping("/{chapterIds}")
    public AjaxResult remove(@PathVariable Long[] chapterIds) {
        return toAjax(scCourseChapterService.deleteScCourseChapterByChapterIds(chapterIds));
    }
}
