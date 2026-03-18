package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScUserProfile;

/**
 * 智慧校园用户扩展档案 Mapper
 *
 * @author can
 */
public interface ScUserProfileMapper {
    ScUserProfile selectScUserProfileByProfileId(Long profileId);

    ScUserProfile selectScUserProfileByUserId(Long userId);

    List<ScUserProfile> selectScUserProfileList(ScUserProfile scUserProfile);

    int insertScUserProfile(ScUserProfile scUserProfile);

    int updateScUserProfile(ScUserProfile scUserProfile);

    int deleteScUserProfileByProfileId(Long profileId);

    int deleteScUserProfileByProfileIds(Long[] profileIds);

    int deleteScUserProfileByUserId(Long userId);

    int deleteScUserProfileByUserIds(Long[] userIds);
}
