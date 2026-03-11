package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScLearningProfile;

public interface IScLearningProfileService
{
    ScLearningProfile selectScLearningProfileByProfileId(Long profileId);
    List<ScLearningProfile> selectScLearningProfileList(ScLearningProfile scLearningProfile);
    int insertScLearningProfile(ScLearningProfile scLearningProfile);
    int updateScLearningProfile(ScLearningProfile scLearningProfile);
    int deleteScLearningProfileByProfileIds(Long[] profileIds);
    int deleteScLearningProfileByProfileId(Long profileId);
    ScLearningProfile rebuildProfile(Long userId, Long courseId);
}
