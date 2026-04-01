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
import com.smart.system.domain.ScStudentStatusLog;
import com.smart.system.service.IScStudentStatusLogService;

@RestController
@RequestMapping("/campus/affair/status")
public class ScStudentStatusLogController extends BaseController {
    @Autowired
    private IScStudentStatusLogService scStudentStatusLogService;

    @PreAuthorize("@ss.hasPermi('campus:studentStatus:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScStudentStatusLog scStudentStatusLog) {
        startPage();
        List<ScStudentStatusLog> list = scStudentStatusLogService.selectScStudentStatusLogList(scStudentStatusLog);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:studentStatus:query')")
    @GetMapping("/{statusLogId}")
    public AjaxResult getInfo(@PathVariable Long statusLogId) {
        return success(scStudentStatusLogService.selectScStudentStatusLogByStatusLogId(statusLogId));
    }
}
