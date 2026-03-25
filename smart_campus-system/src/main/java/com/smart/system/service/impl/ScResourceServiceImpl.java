package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.core.domain.entity.SysUser;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScResourceAsset;
import com.smart.system.domain.ScResourceComment;
import com.smart.system.domain.ScResourceCommentLike;
import com.smart.system.domain.ScResourceFavorite;
import com.smart.system.domain.ScResourceNode;
import com.smart.system.domain.ScResourceRating;
import com.smart.system.domain.campusvo.ResourceInsightVo;
import com.smart.system.domain.campusvo.ResourceNodeTreeVo;
import com.smart.system.domain.campusvo.ResourceOperationOverviewVo;
import com.smart.system.mapper.ScResourceAssetMapper;
import com.smart.system.mapper.ScResourceCommentMapper;
import com.smart.system.mapper.ScResourceCommentLikeMapper;
import com.smart.system.mapper.ScResourceFavoriteMapper;
import com.smart.system.mapper.ScResourceMapper;
import com.smart.system.mapper.ScResourceNodeMapper;
import com.smart.system.mapper.ScResourceRatingMapper;
import com.smart.system.service.IScResourceService;
import com.smart.system.service.ISysUserService;

@Service
public class ScResourceServiceImpl implements IScResourceService {
    @Autowired
    private ScResourceMapper scResourceMapper;
    @Autowired
    private ScResourceNodeMapper scResourceNodeMapper;
    @Autowired
    private ScResourceAssetMapper scResourceAssetMapper;
    @Autowired
    private ScResourceCommentMapper scResourceCommentMapper;
    @Autowired
    private ScResourceCommentLikeMapper scResourceCommentLikeMapper;
    @Autowired
    private ScResourceFavoriteMapper scResourceFavoriteMapper;
    @Autowired
    private ScResourceRatingMapper scResourceRatingMapper;
    @Autowired
    private ISysUserService sysUserService;

    @Override
    public ScResource selectScResourceByResourceId(Long resourceId) {
        return scResourceMapper.selectScResourceByResourceId(resourceId);
    }

    @Override
    public List<ScResource> selectScResourceList(ScResource scResource) {
        return scResourceMapper.selectScResourceList(scResource);
    }

    @Override
    public int insertScResource(ScResource scResource) {
        normalizeResource(scResource);
        return scResourceMapper.insertScResource(scResource);
    }

    @Override
    public int updateScResource(ScResource scResource) {
        normalizeResource(scResource);
        return scResourceMapper.updateScResource(scResource);
    }

    @Override
    public int deleteScResourceByResourceIds(Long[] resourceIds) {
        if (resourceIds == null || resourceIds.length == 0) {
            return 0;
        }
        for (Long resourceId : resourceIds) {
            scResourceAssetMapper.deleteScResourceAssetByResourceId(resourceId);
        }
        return scResourceMapper.deleteScResourceByResourceIds(resourceIds);
    }

    @Override
    public int deleteScResourceByResourceId(Long resourceId) {
        scResourceAssetMapper.deleteScResourceAssetByResourceId(resourceId);
        return scResourceMapper.deleteScResourceByResourceId(resourceId);
    }

    @Override
    public ResourceOperationOverviewVo getResourceOperationOverview(Long courseId, Integer limit) {
        int size = (limit == null || limit <= 0) ? 5 : limit;
        ScResource query = new ScResource();
        query.setCourseId(courseId);
        List<ScResource> resources = scResourceMapper.selectScResourceList(query);

        ResourceOperationOverviewVo vo = new ResourceOperationOverviewVo();
        vo.setTotalResourceCount(resources.size());
        vo.setPublishedResourceCount((int) resources.stream().filter(item -> "0".equals(item.getStatus())).count());
        vo.setPendingAuditCount((int) resources.stream().filter(item -> "0".equals(item.getAuditStatus())).count());
        vo.setHighPotentialCount((int) resources.stream()
                .filter(item -> calculateHeatScore(item).compareTo(BigDecimal.valueOf(75)) >= 0)
                .count());
        vo.setAvgQualityScore(avg(resources.stream().map(item -> defaultQuality(item.getQualityScore())).toList()));
        vo.setAvgHeatScore(avg(resources.stream().map(this::calculateHeatScore).toList()));
        vo.setResourceTypeDistribution(buildTypeDistribution(resources));
        vo.setHottestResources(buildResourceInsights(resources, size, false));
        vo.setRecommendedResources(buildResourceInsights(resources, size, true));
        return vo;
    }

    @Override
    public List<ResourceNodeTreeVo> getCourseResourceTree(Long courseId) {
        ScResourceNode query = new ScResourceNode();
        query.setCourseId(courseId);
        query.setStatus("0");
        List<ScResourceNode> nodes = scResourceNodeMapper.selectScResourceNodeList(query);

        ScResource resourceQuery = new ScResource();
        resourceQuery.setCourseId(courseId);
        resourceQuery.setStatus("0");
        List<ScResource> resources = scResourceMapper.selectScResourceList(resourceQuery);
        Map<Long, Integer> articleCountMap = resources.stream()
                .filter(item -> item.getNodeId() != null)
                .collect(Collectors.toMap(ScResource::getNodeId, item -> 1, Integer::sum));

        Map<Long, ResourceNodeTreeVo> voMap = new LinkedHashMap<>();
        List<ResourceNodeTreeVo> roots = new ArrayList<>();
        for (ScResourceNode node : nodes) {
            ResourceNodeTreeVo vo = new ResourceNodeTreeVo();
            vo.setNodeId(node.getNodeId());
            vo.setCourseId(node.getCourseId());
            vo.setParentId(node.getParentId());
            vo.setNodeName(node.getNodeName());
            vo.setSortNo(node.getSortNo());
            vo.setArticleCount(articleCountMap.getOrDefault(node.getNodeId(), 0));
            voMap.put(node.getNodeId(), vo);
        }
        for (ScResourceNode node : nodes) {
            ResourceNodeTreeVo current = voMap.get(node.getNodeId());
            if (node.getParentId() == null || node.getParentId() == 0 || !voMap.containsKey(node.getParentId())) {
                roots.add(current);
            } else {
                voMap.get(node.getParentId()).getChildren().add(current);
            }
        }
        return roots;
    }

    @Override
    public int initCourseResourceRoot(Long courseId) {
        if (courseId == null) {
            return 0;
        }
        ScResourceNode query = new ScResourceNode();
        query.setCourseId(courseId);
        List<ScResourceNode> exists = scResourceNodeMapper.selectScResourceNodeList(query);
        if (!exists.isEmpty()) {
            return 0;
        }
        ScResourceNode root = new ScResourceNode();
        root.setCourseId(courseId);
        root.setParentId(0L);
        root.setNodeName("课程资源");
        root.setSortNo(1);
        root.setExpandDefault("1");
        root.setStatus("0");
        root.setCreateBy("system");
        return scResourceNodeMapper.insertScResourceNode(root);
    }

    @Override
    public int insertScResourceNode(ScResourceNode scResourceNode) {
        normalizeNode(scResourceNode);
        return scResourceNodeMapper.insertScResourceNode(scResourceNode);
    }

    @Override
    public int updateScResourceNode(ScResourceNode scResourceNode) {
        normalizeNode(scResourceNode);
        return scResourceNodeMapper.updateScResourceNode(scResourceNode);
    }

    @Override
    public int deleteScResourceNodeByNodeId(Long nodeId) {
        ScResource query = new ScResource();
        query.setNodeId(nodeId);
        List<ScResource> resources = scResourceMapper.selectScResourceList(query);
        if (!resources.isEmpty()) {
            return 0;
        }
        ScResourceNode childQuery = new ScResourceNode();
        childQuery.setParentId(nodeId);
        List<ScResourceNode> children = scResourceNodeMapper.selectScResourceNodeList(childQuery);
        if (!children.isEmpty()) {
            return 0;
        }
        return scResourceNodeMapper.deleteScResourceNodeByNodeId(nodeId);
    }

    @Override
    public List<ScResourceAsset> selectScResourceAssetList(ScResourceAsset scResourceAsset) {
        return scResourceAssetMapper.selectScResourceAssetList(scResourceAsset);
    }

    @Override
    public int insertScResourceAsset(ScResourceAsset scResourceAsset) {
        normalizeAsset(scResourceAsset);
        return scResourceAssetMapper.insertScResourceAsset(scResourceAsset);
    }

    @Override
    public int updateScResourceAsset(ScResourceAsset scResourceAsset) {
        normalizeAsset(scResourceAsset);
        return scResourceAssetMapper.updateScResourceAsset(scResourceAsset);
    }

    @Override
    public int deleteScResourceAssetByAssetId(Long assetId) {
        return scResourceAssetMapper.deleteScResourceAssetByAssetId(assetId);
    }

    @Override
    public List<ScResourceComment> selectScResourceCommentList(Long resourceId, Long userId) {
        ScResourceComment query = new ScResourceComment();
        query.setResourceId(resourceId);
        query.setStatus("0");
        List<ScResourceComment> comments = scResourceCommentMapper.selectScResourceCommentList(query);
        Map<Long, ScResourceComment> commentMap = new LinkedHashMap<>();
        for (ScResourceComment comment : comments) {
            if (StringUtils.isEmpty(comment.getUserName()) && comment.getUserId() != null) {
                SysUser user = sysUserService.selectUserById(comment.getUserId());
                if (user != null) {
                    String displayName = StringUtils.isNotEmpty(user.getNickName()) ? user.getNickName()
                            : (StringUtils.isNotEmpty(user.getRealName()) ? user.getRealName() : user.getUserName());
                    comment.setUserName(displayName);
                }
            }
            comment.setLiked(scResourceCommentLikeMapper.selectByCommentAndUser(comment.getCommentId(), userId) != null);
            commentMap.put(comment.getCommentId(), comment);
        }
        List<ScResourceComment> roots = new ArrayList<>();
        for (ScResourceComment comment : comments) {
            if (comment.getParentId() != null && comment.getParentId() > 0 && commentMap.containsKey(comment.getParentId())) {
                commentMap.get(comment.getParentId()).getReplies().add(comment);
            } else {
                roots.add(comment);
            }
        }
        return roots;
    }

    @Override
    public int addResourceComment(ScResourceComment scResourceComment) {
        if (scResourceComment == null || scResourceComment.getResourceId() == null
                || StringUtils.isEmpty(scResourceComment.getContent())) {
            return 0;
        }
        if (scResourceComment.getParentId() == null) {
            scResourceComment.setParentId(0L);
        }
        if (scResourceComment.getRootId() == null) {
            scResourceComment.setRootId(0L);
        }
        if (scResourceComment.getLikeCount() == null) {
            scResourceComment.setLikeCount(0);
        }
        if (StringUtils.isEmpty(scResourceComment.getStatus())) {
            scResourceComment.setStatus("0");
        }
        if (StringUtils.isEmpty(scResourceComment.getUserName()) && scResourceComment.getUserId() != null) {
            SysUser user = sysUserService.selectUserById(scResourceComment.getUserId());
            if (user != null) {
                String displayName = StringUtils.isNotEmpty(user.getNickName()) ? user.getNickName()
                        : (StringUtils.isNotEmpty(user.getRealName()) ? user.getRealName() : user.getUserName());
                scResourceComment.setUserName(displayName);
            }
        }
        int rows = scResourceCommentMapper.insertScResourceComment(scResourceComment);
        refreshResourceInteractionStats(scResourceComment.getResourceId(), scResourceComment.getCreateBy());
        return rows;
    }

    @Override
    public int toggleCommentLike(Long commentId, Long userId, String createBy) {
        if (commentId == null || userId == null) {
            return 0;
        }
        ScResourceComment comment = scResourceCommentMapper.selectScResourceCommentByCommentId(commentId);
        if (comment == null) {
            return 0;
        }
        ScResourceCommentLike exists = scResourceCommentLikeMapper.selectByCommentAndUser(commentId, userId);
        if (exists != null) {
            scResourceCommentLikeMapper.deleteByCommentAndUser(commentId, userId);
        } else {
            ScResourceCommentLike like = new ScResourceCommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            like.setCreateBy(createBy);
            scResourceCommentLikeMapper.insertScResourceCommentLike(like);
        }
        comment.setLikeCount(scResourceCommentLikeMapper.countByCommentId(commentId));
        comment.setUpdateBy(createBy);
        scResourceCommentMapper.updateScResourceComment(comment);
        return 1;
    }

    @Override
    public boolean isFavoriteResource(Long resourceId, Long userId) {
        if (resourceId == null || userId == null) {
            return false;
        }
        return scResourceFavoriteMapper.selectByResourceAndUser(resourceId, userId) != null;
    }

    @Override
    public int favoriteResource(Long resourceId, Long userId, String createBy) {
        if (resourceId == null || userId == null) {
            return 0;
        }
        if (isFavoriteResource(resourceId, userId)) {
            return 0;
        }
        ScResourceFavorite favorite = new ScResourceFavorite();
        favorite.setResourceId(resourceId);
        favorite.setUserId(userId);
        favorite.setCreateBy(createBy);
        int rows = scResourceFavoriteMapper.insertScResourceFavorite(favorite);
        refreshResourceInteractionStats(resourceId, createBy);
        return rows;
    }

    @Override
    public int unfavoriteResource(Long resourceId, Long userId) {
        if (resourceId == null || userId == null) {
            return 0;
        }
        int rows = scResourceFavoriteMapper.deleteByResourceAndUser(resourceId, userId);
        if (rows > 0) {
            refreshResourceInteractionStats(resourceId, String.valueOf(userId));
        }
        return rows;
    }

    @Override
    public List<ScResource> listFavoriteResources(Long userId) {
        return scResourceFavoriteMapper.selectFavoriteResourceList(userId);
    }

    @Override
    public int rateResource(Long resourceId, Long userId, Integer ratingScore) {
        if (resourceId == null || userId == null || ratingScore == null) {
            return 0;
        }
        int safeRating = Math.max(1, Math.min(5, ratingScore));
        ScResourceRating exists = scResourceRatingMapper.selectScResourceRatingByResourceAndUser(resourceId, userId);
        int rows;
        if (exists == null) {
            ScResourceRating rating = new ScResourceRating();
            rating.setResourceId(resourceId);
            rating.setUserId(userId);
            rating.setRatingScore(safeRating);
            rating.setCreateBy(String.valueOf(userId));
            rows = scResourceRatingMapper.insertScResourceRating(rating);
        } else {
            exists.setRatingScore(safeRating);
            exists.setUpdateBy(String.valueOf(userId));
            rows = scResourceRatingMapper.updateScResourceRating(exists);
        }
        refreshResourceInteractionStats(resourceId, String.valueOf(userId));
        return rows;
    }

    @Override
    public int shareResource(Long resourceId) {
        int rows = scResourceMapper.increaseShareCount(resourceId);
        refreshResourceInteractionStats(resourceId, "share");
        return rows;
    }

    private void normalizeNode(ScResourceNode scResourceNode) {
        if (scResourceNode == null) {
            return;
        }
        if (scResourceNode.getParentId() == null) {
            scResourceNode.setParentId(0L);
        }
        if (scResourceNode.getSortNo() == null) {
            scResourceNode.setSortNo(0);
        }
        if (StringUtils.isEmpty(scResourceNode.getExpandDefault())) {
            scResourceNode.setExpandDefault("1");
        }
        if (StringUtils.isEmpty(scResourceNode.getStatus())) {
            scResourceNode.setStatus("0");
        }
    }

    private void normalizeResource(ScResource scResource) {
        if (scResource == null) {
            return;
        }
        if (StringUtils.isEmpty(scResource.getAuditStatus())) {
            scResource.setAuditStatus("0");
        }
        if (StringUtils.isEmpty(scResource.getStatus())) {
            scResource.setStatus("0");
        }
        if (StringUtils.isEmpty(scResource.getResourceType())) {
            scResource.setResourceType("article");
        }
        if (StringUtils.isEmpty(scResource.getTargetUserType())) {
            scResource.setTargetUserType("student");
        }
        if (StringUtils.isEmpty(scResource.getAllowComment())) {
            scResource.setAllowComment("1");
        }
        if (StringUtils.isEmpty(scResource.getAllowRating())) {
            scResource.setAllowRating("1");
        }
        if (StringUtils.isEmpty(scResource.getAllowShare())) {
            scResource.setAllowShare("1");
        }
        if (scResource.getViewCount() == null) {
            scResource.setViewCount(0);
        }
        if (scResource.getDownloadCount() == null) {
            scResource.setDownloadCount(0);
        }
        if (scResource.getFavoriteCount() == null) {
            scResource.setFavoriteCount(0);
        }
        if (scResource.getRatingCount() == null) {
            scResource.setRatingCount(0);
        }
        if (scResource.getCommentCount() == null) {
            scResource.setCommentCount(0);
        }
        if (scResource.getShareCount() == null) {
            scResource.setShareCount(0);
        }
        if (scResource.getSortNo() == null) {
            scResource.setSortNo(0);
        }
        if (scResource.getAvgRating() == null) {
            scResource.setAvgRating(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }
        if (scResource.getQualityScore() == null) {
            scResource.setQualityScore(BigDecimal.valueOf(80));
        }
        if (StringUtils.isEmpty(scResource.getSummary()) && StringUtils.isNotEmpty(scResource.getContentText())) {
            String content = scResource.getContentText().trim();
            scResource.setSummary(content.length() > 120 ? content.substring(0, 120) + "..." : content);
        }
    }

    private void normalizeAsset(ScResourceAsset scResourceAsset) {
        if (scResourceAsset == null) {
            return;
        }
        if (StringUtils.isEmpty(scResourceAsset.getAllowDownload())) {
            scResourceAsset.setAllowDownload("1");
        }
        if (StringUtils.isEmpty(scResourceAsset.getStatus())) {
            scResourceAsset.setStatus("0");
        }
        if (scResourceAsset.getSortNo() == null) {
            scResourceAsset.setSortNo(0);
        }
        if (scResourceAsset.getViewCount() == null) {
            scResourceAsset.setViewCount(0);
        }
        if (scResourceAsset.getDownloadCount() == null) {
            scResourceAsset.setDownloadCount(0);
        }
        if (scResourceAsset.getFavoriteCount() == null) {
            scResourceAsset.setFavoriteCount(0);
        }
    }

    private void refreshResourceInteractionStats(Long resourceId, String updateBy) {
        Map<String, Object> ratingSummary = scResourceRatingMapper.selectRatingSummaryByResourceId(resourceId);
        int commentCount = scResourceCommentMapper.countByResourceId(resourceId);
        int favoriteCount = scResourceFavoriteMapper.countByResourceId(resourceId);
        ScResource current = scResourceMapper.selectScResourceByResourceId(resourceId);
        if (current == null) {
            return;
        }
        ScResource update = new ScResource();
        update.setResourceId(resourceId);
        update.setAvgRating(parseDecimal(ratingSummary.get("avgRating")));
        update.setRatingCount(parseInteger(ratingSummary.get("ratingCount")));
        update.setCommentCount(commentCount);
        update.setFavoriteCount(favoriteCount);
        update.setShareCount(current.getShareCount());
        update.setUpdateBy(updateBy);
        scResourceMapper.updateResourceInteractionStats(update);
    }

    private List<ResourceInsightVo> buildResourceInsights(List<ScResource> resources, int limit, boolean byRecommendation) {
        return resources.stream()
                .map(this::toInsight)
                .sorted((left, right) -> byRecommendation
                        ? safeScore(right.getRecommendationScore()).compareTo(safeScore(left.getRecommendationScore()))
                        : safeScore(right.getHeatScore()).compareTo(safeScore(left.getHeatScore())))
                .limit(limit)
                .toList();
    }

    private ResourceInsightVo toInsight(ScResource resource) {
        ResourceInsightVo vo = new ResourceInsightVo();
        vo.setResourceId(resource.getResourceId());
        vo.setResourceName(resource.getResourceName());
        vo.setResourceType(resource.getResourceType());
        vo.setCourseId(resource.getCourseId());
        vo.setQualityScore(defaultQuality(resource.getQualityScore()));
        vo.setHeatScore(calculateHeatScore(resource));
        vo.setRecommendationScore(calculateRecommendationPotential(resource));
        vo.setInteractionCount(safeCount(resource.getViewCount()) + safeCount(resource.getDownloadCount())
                + safeCount(resource.getFavoriteCount()) + safeCount(resource.getCommentCount())
                + safeCount(resource.getShareCount()));
        vo.setSummary(StringUtils.defaultIfEmpty(resource.getSummary(), "建议补充课时摘要，方便学生快速判断学习内容"));
        vo.setRecommendationReason(buildResourceReason(resource));
        return vo;
    }

    private Map<String, Integer> buildTypeDistribution(List<ScResource> resources) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        for (ScResource resource : resources) {
            String key = StringUtils.defaultIfEmpty(resource.getResourceType(), "article");
            distribution.put(key, distribution.getOrDefault(key, 0) + 1);
        }
        return distribution;
    }

    private BigDecimal calculateHeatScore(ScResource resource) {
        BigDecimal quality = defaultQuality(resource.getQualityScore());
        BigDecimal interactionScore = BigDecimal.valueOf(safeCount(resource.getViewCount()) * 0.4
                + safeCount(resource.getDownloadCount()) * 1.2
                + safeCount(resource.getFavoriteCount()) * 1.8
                + safeCount(resource.getCommentCount()) * 1.5
                + safeCount(resource.getShareCount()) * 2.0);
        return quality.multiply(BigDecimal.valueOf(0.55))
                .add(interactionScore.min(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(0.45)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRecommendationPotential(ScResource resource) {
        BigDecimal base = calculateHeatScore(resource).multiply(BigDecimal.valueOf(0.55))
                .add(defaultQuality(resource.getQualityScore()).multiply(BigDecimal.valueOf(0.25)))
                .add(safeScore(resource.getAvgRating()).multiply(BigDecimal.valueOf(4)).min(BigDecimal.valueOf(20)));
        if (safeCount(resource.getCommentCount()) >= 3) {
            base = base.add(BigDecimal.valueOf(4));
        }
        if (StringUtils.isEmpty(resource.getSummary())) {
            base = base.subtract(BigDecimal.valueOf(5));
        }
        if (!"1".equals(resource.getAuditStatus())) {
            base = base.subtract(BigDecimal.valueOf(10));
        }
        return base.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private String buildResourceReason(ScResource resource) {
        if (!"1".equals(resource.getAuditStatus())) {
            return "文章尚未完成审核，建议先处理审核状态";
        }
        if (safeCount(resource.getShareCount()) >= 3) {
            return "分享传播表现较好，适合作为重点内容持续展示";
        }
        if (safeCount(resource.getCommentCount()) >= 3) {
            return "评论互动较活跃，说明内容具备较强讨论价值";
        }
        if (safeScore(resource.getAvgRating()).compareTo(BigDecimal.valueOf(4.5)) >= 0) {
            return "学生评分表现较好，可作为优先推荐课时";
        }
        return "建议继续补充资源资产和导学摘要，提升学习页完整度";
    }

    private BigDecimal avg(List<BigDecimal> scores) {
        if (scores == null || scores.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal score : scores) {
            total = total.add(safeScore(score));
        }
        return total.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultQuality(BigDecimal qualityScore) {
        return qualityScore == null ? BigDecimal.valueOf(80) : qualityScore;
    }

    private int safeCount(Integer count) {
        return count == null ? 0 : count;
    }

    private BigDecimal safeScore(BigDecimal score) {
        return score == null ? BigDecimal.ZERO : score;
    }

    private BigDecimal parseDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
    }

    private Integer parseInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
