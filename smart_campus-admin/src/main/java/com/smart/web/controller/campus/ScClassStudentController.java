package com.smart.web.controller.campus;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.smart.common.annotation.Log;
import com.smart.common.core.controller.BaseController;
import com.smart.common.core.domain.AjaxResult;
import com.smart.common.core.page.TableDataInfo;
import com.smart.common.enums.BusinessType;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.service.IScUserProfileService;

/**
 * 班级学生关系 Controller
 */
@RestController
@RequestMapping("/campus/classStudent")
public class ScClassStudentController extends BaseController {
    @Autowired
    private IScUserProfileService scUserProfileService;

    @PreAuthorize("@ss.hasPermi('campus:userProfile:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScUserProfile scUserProfile) {
        scUserProfile.setUserType("student");
        startPage();
        List<ScUserProfile> list = scUserProfileService.selectScUserProfileList(scUserProfile);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('campus:userProfile:edit')")
    @Log(title = "班级学生关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult assign(@RequestBody ScUserProfile scUserProfile) {
        if (scUserProfile == null || scUserProfile.getUserId() == null) {
            throw new ServiceException("学生用户不能为空");
        }
        ScUserProfile current = scUserProfileService.selectScUserProfileByUserId(scUserProfile.getUserId());
        if (current == null) {
            throw new ServiceException("学生档案不存在");
        }
        current.setUserType("student");
        if (scUserProfile.getClassId() != null) {
            current.setClassId(scUserProfile.getClassId());
        }
        if (scUserProfile.getGradeId() != null) {
            current.setGradeId(scUserProfile.getGradeId());
        }
        if (StringUtils.isNotEmpty(scUserProfile.getStatus())) {
            current.setStatus(scUserProfile.getStatus());
        }
        if (scUserProfile.getRemark() != null) {
            current.setRemark(scUserProfile.getRemark());
        }
        current.setUpdateBy(getUsername());
        return toAjax(scUserProfileService.updateScUserProfile(current));
    }
}
