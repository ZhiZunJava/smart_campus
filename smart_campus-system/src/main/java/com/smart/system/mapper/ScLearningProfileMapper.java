package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScLearningProfile;

public interface ScLearningProfileMapper {
    ScLearningProfile selectScLearningProfileByProfileId(Long profileId);

    List<ScLearningProfile> selectScLearningProfileList(ScLearningProfile scLearningProfile);

    int insertScLearningProfile(ScLearningProfile scLearningProfile);

    int updateScLearningProfile(ScLearningProfile scLearningProfile);

    int deleteScLearningProfileByProfileId(Long profileId);

    int deleteScLearningProfileByProfileIds(Long[] profileIds);
}
