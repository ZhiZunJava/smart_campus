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
import com.smart.system.domain.ScAffairCategory;
import com.smart.system.service.IScAffairCategoryService;

@RestController
@RequestMapping("/campus/affair/category")
public class ScAffairCategoryController extends BaseController {
    @Autowired
    private IScAffairCategoryService scAffairCategoryService;

    @PreAuthorize("@ss.hasPermi('campus:affairCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScAffairCategory scAffairCategory) {
        startPage();
        List<ScAffairCategory> list = scAffairCategoryService.selectScAffairCategoryList(scAffairCategory);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:affairCategory:query')")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId) {
        return success(scAffairCategoryService.selectScAffairCategoryByCategoryId(categoryId));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairCategory:add')")
    @Log(title = "学生事务分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScAffairCategory scAffairCategory) {
        scAffairCategory.setCreateBy(getUsername());
        return toAjax(scAffairCategoryService.insertScAffairCategory(scAffairCategory));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairCategory:edit')")
    @Log(title = "学生事务分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScAffairCategory scAffairCategory) {
        scAffairCategory.setUpdateBy(getUsername());
        return toAjax(scAffairCategoryService.updateScAffairCategory(scAffairCategory));
    }

    @PreAuthorize("@ss.hasPermi('campus:affairCategory:remove')")
    @Log(title = "学生事务分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds) {
        return toAjax(scAffairCategoryService.deleteScAffairCategoryByCategoryIds(categoryIds));
    }
}
