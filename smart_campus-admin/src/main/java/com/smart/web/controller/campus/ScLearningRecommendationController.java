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
import com.smart.system.domain.ScLearningRecommendation;
import com.smart.system.domain.dto.RecommendationFeedbackDto;
import com.smart.system.service.IScLearningRecommendationService;

@RestController
@RequestMapping("/campus/learningRecommendation")
public class ScLearningRecommendationController extends BaseController {
    @Autowired
    private IScLearningRecommendationService scLearningRecommendationService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public TableDataInfo list(ScLearningRecommendation scLearningRecommendation) {
        startPage();
        List<ScLearningRecommendation> list = scLearningRecommendationService
                .selectScLearningRecommendationList(scLearningRecommendation);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:learningRecommendation:query')")
    @GetMapping(value = "/{recommendId}")
    public AjaxResult getInfo(@PathVariable Long recommendId) {
        return success(scLearningRecommendationService.selectScLearningRecommendationByRecommendId(recommendId));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningRecommendation:add')")
    @Log(title = "个性化推荐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScLearningRecommendation scLearningRecommendation) {
        scLearningRecommendation.setCreateBy(getUsername());
        return toAjax(scLearningRecommendationService.insertScLearningRecommendation(scLearningRecommendation));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningRecommendation:edit')")
    @Log(title = "个性化推荐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScLearningRecommendation scLearningRecommendation) {
        scLearningRecommendation.setUpdateBy(getUsername());
        return toAjax(scLearningRecommendationService.updateScLearningRecommendation(scLearningRecommendation));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningRecommendation:add')")
    @PostMapping("/generate")
    public AjaxResult generateRecommendations(Long userId, String sceneCode, Integer limit) {
        return success(scLearningRecommendationService.generateRecommendations(userId, sceneCode, limit));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public AjaxResult active(Long userId,
            @RequestParam(required = false) String sceneCode,
            @RequestParam(required = false) Integer limit) {
        return success(scLearningRecommendationService.listActiveRecommendations(userId, sceneCode, limit));
    }

    @PostMapping("/feedback")
    public AjaxResult feedback(@RequestBody RecommendationFeedbackDto feedbackDto) {
        return toAjax(scLearningRecommendationService.feedbackRecommendation(feedbackDto));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningRecommendation:remove')")
    @Log(title = "个性化推荐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recommendIds}")
    public AjaxResult remove(@PathVariable Long[] recommendIds) {
        return toAjax(scLearningRecommendationService.deleteScLearningRecommendationByRecommendIds(recommendIds));
    }
}
