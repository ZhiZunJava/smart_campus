package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.common.core.domain.model.LoginUser;
import com.smart.system.domain.ScAffairRequest;
import com.smart.system.domain.dto.AffairBatchReviewDto;
import com.smart.system.domain.dto.AffairRequestReviewDto;
import com.smart.system.domain.dto.AffairRequestSubmitDto;

public interface IScAffairRequestService {
    ScAffairRequest selectScAffairRequestByRequestId(Long requestId);

    List<ScAffairRequest> selectScAffairRequestList(ScAffairRequest scAffairRequest);

    Map<String, Object> selectRequestDetail(Long requestId, LoginUser loginUser);

    Map<String, Object> submitRequest(AffairRequestSubmitDto dto, LoginUser loginUser, String portalRole);

    Map<String, Object> cancelRequest(Long requestId, LoginUser loginUser);

    Map<String, Object> reviewRequest(AffairRequestReviewDto dto, LoginUser loginUser);

    Map<String, Object> batchReviewRequests(AffairBatchReviewDto dto, LoginUser loginUser);

    List<Map<String, Object>> buildServiceCatalog(String portalRole, LoginUser loginUser);

    Map<String, Object> buildPortalDashboard(LoginUser loginUser, String portalRole);

    List<ScAffairRequest> listMyRequests(LoginUser loginUser, String portalRole);

    List<ScAffairRequest> listPendingReviews(LoginUser loginUser, String portalRole);

    Map<String, Object> buildPortalOptions();

    Map<String, Object> buildAffairStatistics(LoginUser loginUser, String portalRole);

    Map<String, Object> buildCategoryDetail(LoginUser loginUser, String portalRole, String categoryCode);

    List<ScAffairRequest> listRecentActivity(LoginUser loginUser, String portalRole, int limit);

    List<Map<String, Object>> listFrequentTemplates(LoginUser loginUser, String portalRole, int limit);

    List<ScAffairRequest> exportRequestList(ScAffairRequest query);
}
