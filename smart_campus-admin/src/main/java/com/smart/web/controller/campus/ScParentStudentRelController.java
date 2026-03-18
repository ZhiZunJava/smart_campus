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
import com.smart.system.domain.ScParentStudentRel;
import com.smart.system.service.IScParentStudentRelService;

/**
 * 家长学生关系 Controller
 *
 * @author can
 */
@RestController
@RequestMapping("/campus/parentStudentRel")
public class ScParentStudentRelController extends BaseController {
    @Autowired
    private IScParentStudentRelService scParentStudentRelService;

    @PreAuthorize("@ss.hasPermi('campus:parentStudentRel:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScParentStudentRel scParentStudentRel) {
        startPage();
        List<ScParentStudentRel> list = scParentStudentRelService.selectScParentStudentRelList(scParentStudentRel);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:parentStudentRel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(scParentStudentRelService.selectScParentStudentRelById(id));
    }

    @PreAuthorize("@ss.hasPermi('campus:parentStudentRel:add')")
    @Log(title = "家长学生关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScParentStudentRel scParentStudentRel) {
        scParentStudentRel.setCreateBy(getUsername());
        return toAjax(scParentStudentRelService.insertScParentStudentRel(scParentStudentRel));
    }

    @PreAuthorize("@ss.hasPermi('campus:parentStudentRel:edit')")
    @Log(title = "家长学生关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScParentStudentRel scParentStudentRel) {
        scParentStudentRel.setUpdateBy(getUsername());
        return toAjax(scParentStudentRelService.updateScParentStudentRel(scParentStudentRel));
    }

    @PreAuthorize("@ss.hasPermi('campus:parentStudentRel:remove')")
    @Log(title = "家长学生关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(scParentStudentRelService.deleteScParentStudentRelByIds(ids));
    }
}
