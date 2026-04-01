package com.smart.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.smart.system.domain.ScAffairRequest;

public interface ScAffairRequestMapper {
    ScAffairRequest selectScAffairRequestByRequestId(Long requestId);

    List<ScAffairRequest> selectScAffairRequestList(ScAffairRequest scAffairRequest);

    int insertScAffairRequest(ScAffairRequest scAffairRequest);

    int updateScAffairRequest(ScAffairRequest scAffairRequest);

    List<ScAffairRequest> selectPendingReviewsByReviewer(@Param("reviewerUserId") Long reviewerUserId,
                                                         @Param("reviewerRoles") List<String> reviewerRoles);

    List<ScAffairRequest> selectRecentActivityByUser(@Param("applicantUserId") Long applicantUserId,
                                                      @Param("limit") int limit);

    List<Map<String, Object>> selectStatisticsByCategory(@Param("applicantUserId") Long applicantUserId);

    List<Map<String, Object>> selectStatisticsByStatus(@Param("applicantUserId") Long applicantUserId);

    List<Map<String, Object>> selectDailyTrend(@Param("applicantUserId") Long applicantUserId,
                                                @Param("days") int days);

    List<Map<String, Object>> selectFrequentTemplates(@Param("applicantUserId") Long applicantUserId,
                                                       @Param("limit") int limit);

    List<ScAffairRequest> selectRequestsByCategoryCode(@Param("applicantUserId") Long applicantUserId,
                                                        @Param("categoryCode") String categoryCode);
}
