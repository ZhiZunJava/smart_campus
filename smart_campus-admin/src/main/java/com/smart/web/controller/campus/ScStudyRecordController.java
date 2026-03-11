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
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.service.IScStudyRecordService;

@RestController
@RequestMapping("/campus/studyRecord")
public class ScStudyRecordController extends BaseController
{
    @Autowired
    private IScStudyRecordService scStudyRecordService;

    @PreAuthorize("@ss.hasPermi('campus:studyRecord:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScStudyRecord scStudyRecord)
    {
        startPage();
        List<ScStudyRecord> list = scStudyRecordService.selectScStudyRecordList(scStudyRecord);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:studyRecord:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable Long recordId)
    {
        return success(scStudyRecordService.selectScStudyRecordByRecordId(recordId));
    }

    @PreAuthorize("@ss.hasPermi('campus:studyRecord:add')")
    @Log(title = "学习行为记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScStudyRecord scStudyRecord)
    {
        scStudyRecord.setCreateBy(getUsername());
        return toAjax(scStudyRecordService.insertScStudyRecord(scStudyRecord));
    }

    @PreAuthorize("@ss.hasPermi('campus:studyRecord:add')")
    @Log(title = "学习行为记录", businessType = BusinessType.INSERT)
    @PostMapping("/recordBehavior")
    public AjaxResult recordBehavior(@Validated @RequestBody ScStudyRecord scStudyRecord)
    {
        scStudyRecord.setCreateBy(getUsername());
        return toAjax(scStudyRecordService.recordBehavior(scStudyRecord));
    }

    @PreAuthorize("@ss.hasPermi('campus:studyRecord:edit')")
    @Log(title = "学习行为记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScStudyRecord scStudyRecord)
    {
        scStudyRecord.setUpdateBy(getUsername());
        return toAjax(scStudyRecordService.updateScStudyRecord(scStudyRecord));
    }

    @PreAuthorize("@ss.hasPermi('campus:studyRecord:remove')")
    @Log(title = "学习行为记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(scStudyRecordService.deleteScStudyRecordByRecordIds(recordIds));
    }
}
