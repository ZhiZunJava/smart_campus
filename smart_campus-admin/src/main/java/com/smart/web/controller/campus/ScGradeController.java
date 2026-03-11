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
import com.smart.system.domain.ScGrade;
import com.smart.system.service.IScGradeService;

@RestController
@RequestMapping("/campus/grade")
public class ScGradeController extends BaseController
{
    @Autowired
    private IScGradeService scGradeService;

    @PreAuthorize("@ss.hasPermi('campus:grade:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScGrade scGrade)
    {
        startPage();
        List<ScGrade> list = scGradeService.selectScGradeList(scGrade);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:grade:query')")
    @GetMapping(value = "/{gradeId}")
    public AjaxResult getInfo(@PathVariable Long gradeId)
    {
        return success(scGradeService.selectScGradeByGradeId(gradeId));
    }

    @PreAuthorize("@ss.hasPermi('campus:grade:add')")
    @Log(title = "年级管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScGrade scGrade)
    {
        scGrade.setCreateBy(getUsername());
        return toAjax(scGradeService.insertScGrade(scGrade));
    }

    @PreAuthorize("@ss.hasPermi('campus:grade:edit')")
    @Log(title = "年级管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScGrade scGrade)
    {
        scGrade.setUpdateBy(getUsername());
        return toAjax(scGradeService.updateScGrade(scGrade));
    }

    @PreAuthorize("@ss.hasPermi('campus:grade:remove')")
    @Log(title = "年级管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{gradeIds}")
    public AjaxResult remove(@PathVariable Long[] gradeIds)
    {
        return toAjax(scGradeService.deleteScGradeByGradeIds(gradeIds));
    }
}
