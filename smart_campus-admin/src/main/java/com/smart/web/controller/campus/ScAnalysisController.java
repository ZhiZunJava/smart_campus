package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.system.domain.ScLearningReport;
import com.smart.system.domain.ScLearningWarning;
import com.smart.system.service.IScLearningReportService;
import com.smart.system.service.IScLearningWarningService;

@RestController
@RequestMapping("/campus/analysis")
public class ScAnalysisController extends BaseController
{
    @Autowired private IScLearningWarningService scLearningWarningService;
    @Autowired private IScLearningReportService scLearningReportService;

    @PreAuthorize("@ss.hasPermi('campus:analysis:warning:list')")
    @GetMapping("/warning/list")
    public TableDataInfo warningList(ScLearningWarning scLearningWarning)
    {
        startPage();
        List<ScLearningWarning> list = scLearningWarningService.selectScLearningWarningList(scLearningWarning);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:analysis:warning:add')")
    @PostMapping("/warning/build")
    public AjaxResult buildWarning(Long userId, Long courseId)
    {
        return success(scLearningWarningService.buildWarning(userId, courseId));
    }

    @PreAuthorize("@ss.hasPermi('campus:analysis:report:list')")
    @GetMapping("/report/list")
    public TableDataInfo reportList(ScLearningReport scLearningReport)
    {
        startPage();
        List<ScLearningReport> list = scLearningReportService.selectScLearningReportList(scLearningReport);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:analysis:report:add')")
    @PostMapping("/report/generate")
    public AjaxResult generateReport(Long userId, String reportType)
    {
        return success(scLearningReportService.generateReport(userId, reportType));
    }
}
