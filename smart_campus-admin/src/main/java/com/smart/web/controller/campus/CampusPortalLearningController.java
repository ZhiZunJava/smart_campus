package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.system.domain.ScLearningRecommendation;
import com.smart.system.domain.ScLearningReport;
import com.smart.system.domain.ScLearningWarning;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.dto.RecommendationFeedbackDto;
import com.smart.system.service.IScLearningRecommendationService;
import com.smart.system.service.IScLearningReportService;
import com.smart.system.service.IScLearningWarningService;
import com.smart.system.service.IScResourceService;

/**
 * 门户端学习服务接口
 */
@RestController
@RequestMapping("/campus/portal/learning")
public class CampusPortalLearningController extends BaseController {
    @Autowired
    private IScResourceService scResourceService;

    @Autowired
    private IScLearningRecommendationService scLearningRecommendationService;

    @Autowired
    private IScLearningWarningService scLearningWarningService;

    @Autowired
    private IScLearningReportService scLearningReportService;

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

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/recommendation/list")
    public TableDataInfo recommendationList(ScLearningRecommendation scLearningRecommendation) {
        startPage();
        List<ScLearningRecommendation> list = scLearningRecommendationService
                .selectScLearningRecommendationList(scLearningRecommendation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @GetMapping("/recommendation/active")
    public AjaxResult recommendationActive(Long userId, String sceneCode, Integer limit) {
        return success(scLearningRecommendationService.listActiveRecommendations(userId, sceneCode, limit));
    }

    @PreAuthorize("@ss.hasAnyRoles('student,teacher,parent,admin')")
    @PostMapping("/recommendation/feedback")
    public AjaxResult recommendationFeedback(@RequestBody RecommendationFeedbackDto feedbackDto) {
        return toAjax(scLearningRecommendationService.feedbackRecommendation(feedbackDto));
    }

    @PreAuthorize("@ss.hasAnyRoles('teacher,parent,admin')")
    @GetMapping("/warning/list")
    public TableDataInfo warningList(ScLearningWarning scLearningWarning) {
        startPage();
        List<ScLearningWarning> list = scLearningWarningService.selectScLearningWarningList(scLearningWarning);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasAnyRoles('parent,admin')")
    @GetMapping("/report/list")
    public TableDataInfo reportList(ScLearningReport scLearningReport) {
        startPage();
        List<ScLearningReport> list = scLearningReportService.selectScLearningReportList(scLearningReport);
        return getDataTable(list);
    }
}
