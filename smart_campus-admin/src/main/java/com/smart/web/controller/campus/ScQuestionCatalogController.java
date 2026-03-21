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
import com.smart.system.domain.ScQuestionCatalog;
import com.smart.system.service.IScQuestionCatalogService;

@RestController
@RequestMapping("/campus/questionCatalog")
public class ScQuestionCatalogController extends BaseController {
    @Autowired
    private IScQuestionCatalogService scQuestionCatalogService;

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @GetMapping("/list")
    public TableDataInfo list(ScQuestionCatalog scQuestionCatalog) {
        startPage();
        List<ScQuestionCatalog> list = scQuestionCatalogService.selectScQuestionCatalogList(scQuestionCatalog);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @GetMapping("/{catalogId}")
    public AjaxResult getInfo(@PathVariable Long catalogId) {
        return success(scQuestionCatalogService.selectScQuestionCatalogByCatalogId(catalogId));
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @Log(title = "题库管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScQuestionCatalog scQuestionCatalog) {
        scQuestionCatalog.setCreateBy(getUsername());
        return toAjax(scQuestionCatalogService.insertScQuestionCatalog(scQuestionCatalog));
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @Log(title = "题库管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScQuestionCatalog scQuestionCatalog) {
        scQuestionCatalog.setUpdateBy(getUsername());
        return toAjax(scQuestionCatalogService.updateScQuestionCatalog(scQuestionCatalog));
    }

    @PreAuthorize("@ss.hasAnyRoles('admin,teacher')")
    @Log(title = "题库管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{catalogIds}")
    public AjaxResult remove(@PathVariable Long[] catalogIds) {
        return toAjax(scQuestionCatalogService.deleteScQuestionCatalogByCatalogIds(catalogIds));
    }
}
