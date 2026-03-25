package com.smart.system.mapper;

import java.math.BigDecimal;
import java.util.Map;
import com.smart.system.domain.ScResourceRating;

public interface ScResourceRatingMapper {
    ScResourceRating selectScResourceRatingByResourceAndUser(Long resourceId, Long userId);

    int insertScResourceRating(ScResourceRating scResourceRating);

    int updateScResourceRating(ScResourceRating scResourceRating);

    Map<String, Object> selectRatingSummaryByResourceId(Long resourceId);
}
