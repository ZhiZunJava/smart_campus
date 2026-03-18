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
import com.smart.system.domain.ScKnowledgePoint;
import com.smart.system.service.IScKnowledgePointService;

@RestController
@RequestMapping("/campus/knowledgePoint")
public class ScKnowledgePointController extends BaseController {
    @Autowired
    private IScKnowledgePointService scKnowledgePointService;

    @PreAuthorize("@ss.hasPermi('campus:knowledgePoint:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScKnowledgePoint scKnowledgePoint) {
        startPage();
        List<ScKnowledgePoint> list = scKnowledgePointService.selectScKnowledgePointList(scKnowledgePoint);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:knowledgePoint:query')")
    @GetMapping(value = "/{knowledgePointId}")
    public AjaxResult getInfo(@PathVariable Long knowledgePointId) {
        return success(scKnowledgePointService.selectScKnowledgePointByKnowledgePointId(knowledgePointId));
    }

    @PreAuthorize("@ss.hasPermi('campus:knowledgePoint:add')")
    @Log(title = "知识点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScKnowledgePoint scKnowledgePoint) {
        scKnowledgePoint.setCreateBy(getUsername());
        return toAjax(scKnowledgePointService.insertScKnowledgePoint(scKnowledgePoint));
    }

    @PreAuthorize("@ss.hasPermi('campus:knowledgePoint:edit')")
    @Log(title = "知识点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScKnowledgePoint scKnowledgePoint) {
        scKnowledgePoint.setUpdateBy(getUsername());
        return toAjax(scKnowledgePointService.updateScKnowledgePoint(scKnowledgePoint));
    }

    @PreAuthorize("@ss.hasPermi('campus:knowledgePoint:remove')")
    @Log(title = "知识点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{knowledgePointIds}")
    public AjaxResult remove(@PathVariable Long[] knowledgePointIds) {
        return toAjax(scKnowledgePointService.deleteScKnowledgePointByKnowledgePointIds(knowledgePointIds));
    }
}
