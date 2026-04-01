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
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.dto.UserProfileAdvisorBindDto;
import com.smart.system.service.IScUserProfileService;

/**
 * 智慧校园用户扩展档案 Controller
 *
 * @author can
 */
@RestController
@RequestMapping("/campus/userProfile")
public class ScUserProfileController extends BaseController {
    @Autowired
    private IScUserProfileService scUserProfileService;

    @PreAuthorize("@ss.hasPermi('campus:userProfile:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScUserProfile scUserProfile) {
        startPage();
        List<ScUserProfile> list = scUserProfileService.selectScUserProfileList(scUserProfile);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:userProfile:query')")
    @GetMapping(value = "/{profileId}")
    public AjaxResult getInfo(@PathVariable Long profileId) {
        return success(scUserProfileService.selectScUserProfileByProfileId(profileId));
    }

    @PreAuthorize("@ss.hasPermi('campus:userProfile:add')")
    @Log(title = "用户扩展档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ScUserProfile scUserProfile) {
        scUserProfile.setCreateBy(getUsername());
        return toAjax(scUserProfileService.insertScUserProfile(scUserProfile));
    }

    @PreAuthorize("@ss.hasPermi('campus:userProfile:edit')")
    @Log(title = "用户扩展档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ScUserProfile scUserProfile) {
        scUserProfile.setUpdateBy(getUsername());
        return toAjax(scUserProfileService.updateScUserProfile(scUserProfile));
    }

    @PreAuthorize("@ss.hasPermi('campus:userProfile:edit')")
    @Log(title = "学生档案辅导员绑定", businessType = BusinessType.UPDATE)
    @PutMapping("/bindAdvisor")
    public AjaxResult bindAdvisor(@Validated @RequestBody UserProfileAdvisorBindDto dto) {
        AjaxResult ajax = success("批量绑定完成");
        ajax.put("affectedRows", scUserProfileService.batchBindAdvisor(dto, getUsername()));
        return ajax;
    }

    @PreAuthorize("@ss.hasPermi('campus:userProfile:remove')")
    @Log(title = "用户扩展档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{profileIds}")
    public AjaxResult remove(@PathVariable Long[] profileIds) {
        return toAjax(scUserProfileService.deleteScUserProfileByProfileIds(profileIds));
    }
}
