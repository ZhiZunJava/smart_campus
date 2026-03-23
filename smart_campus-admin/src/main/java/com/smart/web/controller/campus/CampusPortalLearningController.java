package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.system.domain.ScResource;
import com.smart.system.service.IScResourceService;

/**
 * 门户端学习资源接口
 */
@RestController
@RequestMapping("/campus/portal/learning")
public class CampusPortalLearningController extends BaseController {
    @Autowired
    private IScResourceService scResourceService;

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/list")
    public TableDataInfo resourceList(ScResource scResource) {
        startPage();
        List<ScResource> list = scResourceService.selectScResourceList(scResource);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/resource/{resourceId}")
    public AjaxResult resourceDetail(@PathVariable Long resourceId) {
        return success(scResourceService.selectScResourceByResourceId(resourceId));
    }
}
