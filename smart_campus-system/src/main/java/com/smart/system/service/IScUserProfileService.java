package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScUserProfile;

/**
 * 智慧校园用户扩展档案 Service 接口
 *
 * @author Codex
 */
public interface IScUserProfileService
{
    ScUserProfile selectScUserProfileByProfileId(Long profileId);

    List<ScUserProfile> selectScUserProfileList(ScUserProfile scUserProfile);

    int insertScUserProfile(ScUserProfile scUserProfile);

    int updateScUserProfile(ScUserProfile scUserProfile);

    int deleteScUserProfileByProfileIds(Long[] profileIds);

    int deleteScUserProfileByProfileId(Long profileId);
}
