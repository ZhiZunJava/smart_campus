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
import com.smart.system.domain.ScAffairTemplate;
import com.smart.system.service.IScAffairTemplateService;

@RestController
@RequestMapping("/campus/affair/template")
public class ScAffairTemplateController extends BaseController {
    @Autowired
    private IScAffairTemplateService scAffairTemplateService;

    @PreAuthorize("@ss.hasPermi('campus:affairTemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScAffairTemplate scAffairTemplate) {
        startPage();
        List<ScAffairTemplate> list = scAffairTemplateService.selectScAffairTemplateList(scAffairTemplate);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:affairTemplate:query')")
    @GetMapping("/{templateId}")
    public AjaxResult getInfo(@PathVariable Long templateId) {
        return success(scAffairTemplateService.selectScAffairTemplateByTemplateId(templateId));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairTemplate:add')")
    @Log(title = "学生事务模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScAffairTemplate scAffairTemplate) {
        scAffairTemplate.setCreateBy(getUsername());
        return toAjax(scAffairTemplateService.insertScAffairTemplate(scAffairTemplate));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairTemplate:edit')")
    @Log(title = "学生事务模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScAffairTemplate scAffairTemplate) {
        scAffairTemplate.setUpdateBy(getUsername());
        return toAjax(scAffairTemplateService.updateScAffairTemplate(scAffairTemplate));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairTemplate:remove')")
    @Log(title = "学生事务模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds) {
        return toAjax(scAffairTemplateService.deleteScAffairTemplateByTemplateIds(templateIds));
    }
}
