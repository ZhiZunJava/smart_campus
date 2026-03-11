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
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.service.IScLearningProfileService;

@RestController
@RequestMapping("/campus/learningProfile")
public class ScLearningProfileController extends BaseController
{
    @Autowired
    private IScLearningProfileService scLearningProfileService;

    @PreAuthorize("@ss.hasPermi('campus:learningProfile:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScLearningProfile scLearningProfile)
    {
        startPage();
        List<ScLearningProfile> list = scLearningProfileService.selectScLearningProfileList(scLearningProfile);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:learningProfile:query')")
    @GetMapping(value = "/{profileId}")
    public AjaxResult getInfo(@PathVariable Long profileId)
    {
        return success(scLearningProfileService.selectScLearningProfileByProfileId(profileId));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningProfile:add')")
    @Log(title = "学习画像", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScLearningProfile scLearningProfile)
    {
        scLearningProfile.setCreateBy(getUsername());
        return toAjax(scLearningProfileService.insertScLearningProfile(scLearningProfile));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningProfile:edit')")
    @Log(title = "学习画像", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScLearningProfile scLearningProfile)
    {
        scLearningProfile.setUpdateBy(getUsername());
        return toAjax(scLearningProfileService.updateScLearningProfile(scLearningProfile));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningProfile:edit')")
    @PostMapping("/rebuild")
    public AjaxResult rebuildProfile(Long userId, Long courseId)
    {
        return success(scLearningProfileService.rebuildProfile(userId, courseId));
    }

    @PreAuthorize("@ss.hasPermi('campus:learningProfile:remove')")
    @Log(title = "学习画像", businessType = BusinessType.DELETE)
    @DeleteMapping("/{profileIds}")
    public AjaxResult remove(@PathVariable Long[] profileIds)
    {
        return toAjax(scLearningProfileService.deleteScLearningProfileByProfileIds(profileIds));
    }
}
