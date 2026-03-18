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
import com.smart.system.domain.ScLoginRiskEvent;
import com.smart.system.service.IScLoginRiskEventService;

/**
 * 登录风险事件 Controller
 *
 * @author can
 */
@RestController
@RequestMapping("/campus/loginRiskEvent")
public class ScLoginRiskEventController extends BaseController {
    @Autowired
    private IScLoginRiskEventService scLoginRiskEventService;

    @PreAuthorize("@ss.hasPermi('campus:loginRiskEvent:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScLoginRiskEvent scLoginRiskEvent) {
        startPage();
        List<ScLoginRiskEvent> list = scLoginRiskEventService.selectScLoginRiskEventList(scLoginRiskEvent);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:loginRiskEvent:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(scLoginRiskEventService.selectScLoginRiskEventById(id));
    }

    @PreAuthorize("@ss.hasPermi('campus:loginRiskEvent:add')")
    @Log(title = "登录风险事件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScLoginRiskEvent scLoginRiskEvent) {
        scLoginRiskEvent.setCreateBy(getUsername());
        return toAjax(scLoginRiskEventService.insertScLoginRiskEvent(scLoginRiskEvent));
    }

    @PreAuthorize("@ss.hasPermi('campus:loginRiskEvent:edit')")
    @Log(title = "登录风险事件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScLoginRiskEvent scLoginRiskEvent) {
        scLoginRiskEvent.setUpdateBy(getUsername());
        return toAjax(scLoginRiskEventService.updateScLoginRiskEvent(scLoginRiskEvent));
    }

    @PreAuthorize("@ss.hasPermi('campus:loginRiskEvent:remove')")
    @Log(title = "登录风险事件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(scLoginRiskEventService.deleteScLoginRiskEventByIds(ids));
    }
}
