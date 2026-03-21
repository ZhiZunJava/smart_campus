package com.smart.system.service;

import java.util.Map;

public interface IScUserGrowthService {
    void rewardExamSubmit(Long userId, Long recordId, String operator);

    Map<String, Object> buildGrowthSummary(Long userId);
}

