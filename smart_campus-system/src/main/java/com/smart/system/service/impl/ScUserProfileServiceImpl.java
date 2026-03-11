package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.mapper.ScUserProfileMapper;
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

    @Override
    public ScUserProfile selectScUserProfileByProfileId(Long profileId)
    {
        return scUserProfileMapper.selectScUserProfileByProfileId(profileId);
    }

    @Override
    public List<ScUserProfile> selectScUserProfileList(ScUserProfile scUserProfile)
    {
        return scUserProfileMapper.selectScUserProfileList(scUserProfile);
    }

    @Override
    public int insertScUserProfile(ScUserProfile scUserProfile)
    {
        return scUserProfileMapper.insertScUserProfile(scUserProfile);
    }

    @Override
    public int updateScUserProfile(ScUserProfile scUserProfile)
    {
        return scUserProfileMapper.updateScUserProfile(scUserProfile);
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
}
