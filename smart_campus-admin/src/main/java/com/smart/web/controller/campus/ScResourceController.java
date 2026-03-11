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
import com.smart.system.domain.ScResource;
import com.smart.system.service.IScResourceService;

@RestController
@RequestMapping("/campus/resource")
public class ScResourceController extends BaseController
{
    @Autowired
    private IScResourceService scResourceService;

    @PreAuthorize("@ss.hasPermi('campus:resource:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScResource scResource)
    {
        startPage();
        List<ScResource> list = scResourceService.selectScResourceList(scResource);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:query')")
    @GetMapping(value = "/{resourceId}")
    public AjaxResult getInfo(@PathVariable Long resourceId)
    {
        return success(scResourceService.selectScResourceByResourceId(resourceId));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:add')")
    @Log(title = "学习资源", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScResource scResource)
    {
        scResource.setCreateBy(getUsername());
        return toAjax(scResourceService.insertScResource(scResource));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:edit')")
    @Log(title = "学习资源", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScResource scResource)
    {
        scResource.setUpdateBy(getUsername());
        return toAjax(scResourceService.updateScResource(scResource));
    }

    @PreAuthorize("@ss.hasPermi('campus:resource:remove')")
    @Log(title = "学习资源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{resourceIds}")
    public AjaxResult remove(@PathVariable Long[] resourceIds)
    {
        return toAjax(scResourceService.deleteScResourceByResourceIds(resourceIds));
    }
}
