package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.mapper.ScUserProfileMapper;
import com.smart.system.mapper.SysUserMapper;
import com.smart.system.service.IScUserProfileService;

/**
 * 智慧校园用户扩展档案 Service 实现
 *
 * @author Codex
 */
@Service
public class ScUserProfileServiceImpl implements IScUserProfileService
{
    @Autowired
    private ScUserProfileMapper scUserProfileMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public ScUserProfile selectScUserProfileByProfileId(Long profileId)
    {
        return scUserProfileMapper.selectScUserProfileByProfileId(profileId);
    }

    @Override
    public ScUserProfile selectScUserProfileByUserId(Long userId)
    {
        return scUserProfileMapper.selectScUserProfileByUserId(userId);
    }

    @Override
    public List<ScUserProfile> selectScUserProfileList(ScUserProfile scUserProfile)
    {
        return scUserProfileMapper.selectScUserProfileList(scUserProfile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertScUserProfile(ScUserProfile scUserProfile)
    {
        int rows = scUserProfileMapper.insertScUserProfile(scUserProfile);
        syncSystemUserBase(scUserProfile);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateScUserProfile(ScUserProfile scUserProfile)
    {
        if (scUserProfile.getProfileId() == null && scUserProfile.getUserId() != null)
        {
            ScUserProfile current = scUserProfileMapper.selectScUserProfileByUserId(scUserProfile.getUserId());
            if (current != null)
            {
                scUserProfile.setProfileId(current.getProfileId());
            }
        }
        int rows;
        if (scUserProfile.getProfileId() == null)
        {
            rows = scUserProfileMapper.insertScUserProfile(scUserProfile);
        }
        else
        {
            rows = scUserProfileMapper.updateScUserProfile(scUserProfile);
        }
        syncSystemUserBase(scUserProfile);
        return rows;
    }

    @Override
    public int deleteScUserProfileByProfileIds(Long[] profileIds)
    {
        return scUserProfileMapper.deleteScUserProfileByProfileIds(profileIds);
    }

    @Override
    public int deleteScUserProfileByProfileId(Long profileId)
    {
        return scUserProfileMapper.deleteScUserProfileByProfileId(profileId);
    }

    @Override
    public int deleteScUserProfileByUserId(Long userId)
    {
        return scUserProfileMapper.deleteScUserProfileByUserId(userId);
    }

    @Override
    public int deleteScUserProfileByUserIds(Long[] userIds)
    {
        return scUserProfileMapper.deleteScUserProfileByUserIds(userIds);
    }

    @Override
    public ScUserProfile syncUserProfile(SysUser user)
    {
        if (user == null || user.getUserId() == null)
        {
            return null;
        }
        ScUserProfile profile = scUserProfileMapper.selectScUserProfileByUserId(user.getUserId());
        boolean create = profile == null;
        if (create)
        {
            profile = new ScUserProfile();
            profile.setUserId(user.getUserId());
            profile.setCreateBy(user.getCreateBy());
        }
        profile.setUserType(resolveUserType(user));
        profile.setStudentNo(user.getStudentNo());
        profile.setTeacherNo(user.getTeacherNo());
        profile.setRealName(StringUtils.isNotEmpty(user.getRealName()) ? user.getRealName() : user.getNickName());
        profile.setGender(user.getSex());
        profile.setGradeId(user.getGradeId());
        profile.setClassId(user.getClassId());
        profile.setMajor(user.getMajor());
        profile.setAdmissionYear(user.getAdmissionYear());
        profile.setLearningGoal(user.getLearningGoal());
        profile.setInterestTags(user.getInterestTags());
        profile.setLearningStyle(user.getLearningStyle());
        profile.setAvatarUrl(user.getAvatar());
        profile.setStatus(StringUtils.isEmpty(user.getStatus()) ? "0" : user.getStatus());
        profile.setRemark(user.getRemark());
        profile.setUpdateBy(StringUtils.isNotEmpty(user.getUpdateBy()) ? user.getUpdateBy() : user.getCreateBy());
        if (create)
        {
            scUserProfileMapper.insertScUserProfile(profile);
        }
        else
        {
            scUserProfileMapper.updateScUserProfile(profile);
        }
        fillUserProfile(user);
        return profile;
    }

    @Override
    public void fillUserProfile(SysUser user)
    {
        if (user == null || user.getUserId() == null)
        {
            return;
        }
        ScUserProfile profile = scUserProfileMapper.selectScUserProfileByUserId(user.getUserId());
        if (profile == null)
        {
            return;
        }
        user.setProfileId(profile.getProfileId());
        user.setUserType(profile.getUserType());
        user.setStudentNo(profile.getStudentNo());
        user.setTeacherNo(profile.getTeacherNo());
        user.setRealName(profile.getRealName());
        user.setSex(profile.getGender());
        user.setGradeId(profile.getGradeId());
        user.setClassId(profile.getClassId());
        user.setMajor(profile.getMajor());
        user.setAdmissionYear(profile.getAdmissionYear());
        user.setLearningGoal(profile.getLearningGoal());
        user.setInterestTags(profile.getInterestTags());
        user.setLearningStyle(profile.getLearningStyle());
        if (StringUtils.isNotEmpty(profile.getAvatarUrl()))
        {
            user.setAvatar(profile.getAvatarUrl());
        }
        if (StringUtils.isEmpty(user.getRemark()))
        {
            user.setRemark(profile.getRemark());
        }
    }

    private void syncSystemUserBase(ScUserProfile profile)
    {
        if (profile == null || profile.getUserId() == null)
        {
            return;
        }
        SysUser user = sysUserMapper.selectUserById(profile.getUserId());
        if (user == null)
        {
            return;
        }
        if (StringUtils.isNotEmpty(profile.getRealName()))
        {
            user.setNickName(profile.getRealName());
            user.setRealName(profile.getRealName());
        }
        if (StringUtils.isNotEmpty(profile.getGender()))
        {
            user.setSex(profile.getGender());
        }
        if (StringUtils.isNotEmpty(profile.getAvatarUrl()))
        {
            user.setAvatar(profile.getAvatarUrl());
        }
        if (StringUtils.isNotEmpty(profile.getStatus()))
        {
            user.setStatus(profile.getStatus());
        }
        user.setUserType(profile.getUserType());
        user.setStudentNo(profile.getStudentNo());
        user.setTeacherNo(profile.getTeacherNo());
        user.setGradeId(profile.getGradeId());
        user.setClassId(profile.getClassId());
        user.setMajor(profile.getMajor());
        user.setAdmissionYear(profile.getAdmissionYear());
        user.setLearningGoal(profile.getLearningGoal());
        user.setInterestTags(profile.getInterestTags());
        user.setLearningStyle(profile.getLearningStyle());
        if (profile.getRemark() != null)
        {
            user.setRemark(profile.getRemark());
        }
        user.setUpdateBy(profile.getUpdateBy());
        sysUserMapper.updateUser(user);
    }

    private String resolveUserType(SysUser user)
    {
        if (StringUtils.isNotEmpty(user.getUserType()))
        {
            return user.getUserType().trim().toLowerCase();
        }
        return "student";
    }
}
