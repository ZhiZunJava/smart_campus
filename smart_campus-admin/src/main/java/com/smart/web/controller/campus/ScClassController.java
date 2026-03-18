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
import com.smart.system.domain.ScClass;
import com.smart.system.service.IScClassService;

@RestController
@RequestMapping("/campus/class")
public class ScClassController extends BaseController {
    @Autowired
    private IScClassService scClassService;

    @PreAuthorize("@ss.hasPermi('campus:class:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScClass scClass) {
        startPage();
        List<ScClass> list = scClassService.selectScClassList(scClass);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:class:query')")
    @GetMapping(value = "/{classId}")
    public AjaxResult getInfo(@PathVariable Long classId) {
        return success(scClassService.selectScClassByClassId(classId));
    }

    @PreAuthorize("@ss.hasPermi('campus:class:add')")
    @Log(title = "班级管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScClass scClass) {
        scClass.setCreateBy(getUsername());
        return toAjax(scClassService.insertScClass(scClass));
    }

    @PreAuthorize("@ss.hasPermi('campus:class:edit')")
    @Log(title = "班级管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScClass scClass) {
        scClass.setUpdateBy(getUsername());
        return toAjax(scClassService.updateScClass(scClass));
    }

    @PreAuthorize("@ss.hasPermi('campus:class:remove')")
    @Log(title = "班级管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{classIds}")
    public AjaxResult remove(@PathVariable Long[] classIds) {
        return toAjax(scClassService.deleteScClassByClassIds(classIds));
    }
}
