package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScUserAchievement;

public interface ScUserAchievementMapper {
    ScUserAchievement selectScUserAchievementByUserAndCode(Long userId, String achievementCode);

    List<ScUserAchievement> selectScUserAchievementList(ScUserAchievement scUserAchievement);

    int insertScUserAchievement(ScUserAchievement scUserAchievement);

    int updateScUserAchievement(ScUserAchievement scUserAchievement);
}

