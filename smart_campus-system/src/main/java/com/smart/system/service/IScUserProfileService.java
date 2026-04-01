package com.smart.system.service;

import java.util.List;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.system.domain.ScUserProfile;
import com.smart.system.domain.dto.UserProfileAdvisorBindDto;

/**
 * 智慧校园用户扩展档案 Service 接口
 *
 * @author can
 */
public interface IScUserProfileService {
    ScUserProfile selectScUserProfileByProfileId(Long profileId);

    List<ScUserProfile> selectScUserProfileList(ScUserProfile scUserProfile);

    int insertScUserProfile(ScUserProfile scUserProfile);

    int updateScUserProfile(ScUserProfile scUserProfile);

    int deleteScUserProfileByProfileIds(Long[] profileIds);

    int deleteScUserProfileByProfileId(Long profileId);

    ScUserProfile selectScUserProfileByUserId(Long userId);

    int deleteScUserProfileByUserId(Long userId);

    int deleteScUserProfileByUserIds(Long[] userIds);

    ScUserProfile syncUserProfile(SysUser user);

    void fillUserProfile(SysUser user);

    int batchBindAdvisor(UserProfileAdvisorBindDto dto, String operator);
}
