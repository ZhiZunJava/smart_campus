package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScResourceAsset;
import com.smart.system.domain.ScResourceComment;
import com.smart.system.domain.ScResourceNode;
import com.smart.system.domain.campusvo.ResourceNodeTreeVo;
import com.smart.system.domain.campusvo.ResourceOperationOverviewVo;

public interface IScResourceService {
    ScResource selectScResourceByResourceId(Long resourceId);

    List<ScResource> selectScResourceList(ScResource scResource);

    int insertScResource(ScResource scResource);

    int updateScResource(ScResource scResource);

    int deleteScResourceByResourceIds(Long[] resourceIds);

    int deleteScResourceByResourceId(Long resourceId);

    ResourceOperationOverviewVo getResourceOperationOverview(Long courseId, Integer limit);

    List<ResourceNodeTreeVo> getCourseResourceTree(Long courseId);

    int initCourseResourceRoot(Long courseId);

    int insertScResourceNode(ScResourceNode scResourceNode);

    int updateScResourceNode(ScResourceNode scResourceNode);

    int deleteScResourceNodeByNodeId(Long nodeId);

    List<ScResourceAsset> selectScResourceAssetList(ScResourceAsset scResourceAsset);

    int insertScResourceAsset(ScResourceAsset scResourceAsset);

    int updateScResourceAsset(ScResourceAsset scResourceAsset);

    int deleteScResourceAssetByAssetId(Long assetId);

    List<ScResourceComment> selectScResourceCommentList(Long resourceId, Long userId);

    int addResourceComment(ScResourceComment scResourceComment);

    int toggleCommentLike(Long commentId, Long userId, String createBy);

    boolean isFavoriteResource(Long resourceId, Long userId);

    int favoriteResource(Long resourceId, Long userId, String createBy);

    int unfavoriteResource(Long resourceId, Long userId);

    List<ScResource> listFavoriteResources(Long userId);

    int rateResource(Long resourceId, Long userId, Integer ratingScore);

    int shareResource(Long resourceId);
}
