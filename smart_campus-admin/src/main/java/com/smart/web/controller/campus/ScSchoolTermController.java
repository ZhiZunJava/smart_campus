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
import com.smart.system.domain.ScSchoolTerm;
import com.smart.system.service.IScSchoolTermService;

@RestController
@RequestMapping("/campus/schoolTerm")
public class ScSchoolTermController extends BaseController {
    @Autowired
    private IScSchoolTermService scSchoolTermService;

    @PreAuthorize("@ss.hasPermi('campus:grade:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScSchoolTerm scSchoolTerm) {
        startPage();
        List<ScSchoolTerm> list = scSchoolTermService.selectScSchoolTermList(scSchoolTerm);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:grade:query')")
    @GetMapping("/{termId}")
    public AjaxResult getInfo(@PathVariable Long termId) {
        return success(scSchoolTermService.selectScSchoolTermByTermId(termId));
    }

    @PreAuthorize("@ss.hasPermi('campus:grade:add')")
    @Log(title = "学年学期", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScSchoolTerm scSchoolTerm) {
        scSchoolTerm.setCreateBy(getUsername());
        return toAjax(scSchoolTermService.insertScSchoolTerm(scSchoolTerm));
    }

    @PreAuthorize("@ss.hasPermi('campus:grade:edit')")
    @Log(title = "学年学期", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScSchoolTerm scSchoolTerm) {
        scSchoolTerm.setUpdateBy(getUsername());
        return toAjax(scSchoolTermService.updateScSchoolTerm(scSchoolTerm));
    }

    @PreAuthorize("@ss.hasPermi('campus:grade:remove')")
    @Log(title = "学年学期", businessType = BusinessType.DELETE)
    @DeleteMapping("/{termIds}")
    public AjaxResult remove(@PathVariable Long[] termIds) {
        return toAjax(scSchoolTermService.deleteScSchoolTermByTermIds(termIds));
    }
}
