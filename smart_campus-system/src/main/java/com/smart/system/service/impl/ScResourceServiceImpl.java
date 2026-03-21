package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.campusvo.ResourceInsightVo;
import com.smart.system.domain.campusvo.ResourceOperationOverviewVo;
import com.smart.system.mapper.ScResourceMapper;
import com.smart.system.service.IScResourceService;

@Service
public class ScResourceServiceImpl implements IScResourceService {
    @Autowired
    private ScResourceMapper scResourceMapper;

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
        return scResourceMapper.deleteScResourceByResourceIds(resourceIds);
    }

    @Override
    public int deleteScResourceByResourceId(Long resourceId) {
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
        vo.setPublishedResourceCount((int) resources.stream()
                .filter(item -> "0".equals(item.getStatus()))
                .count());
        vo.setPendingAuditCount((int) resources.stream()
                .filter(item -> "0".equals(item.getAuditStatus()))
                .count());
        vo.setHighPotentialCount((int) resources.stream()
                .filter(item -> calculateHeatScore(item).compareTo(BigDecimal.valueOf(75)) >= 0)
                .count());
        vo.setAvgQualityScore(avg(resources.stream()
                .map(item -> defaultQuality(item.getQualityScore()))
                .toList()));
        vo.setAvgHeatScore(avg(resources.stream()
                .map(this::calculateHeatScore)
                .toList()));
        vo.setResourceTypeDistribution(buildTypeDistribution(resources));
        vo.setHottestResources(buildResourceInsights(resources, size, false));
        vo.setRecommendedResources(buildResourceInsights(resources, size, true));
        return vo;
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
        if (scResource.getViewCount() == null) {
            scResource.setViewCount(0);
        }
        if (scResource.getDownloadCount() == null) {
            scResource.setDownloadCount(0);
        }
        if (scResource.getFavoriteCount() == null) {
            scResource.setFavoriteCount(0);
        }
        if (scResource.getQualityScore() == null) {
            scResource.setQualityScore(BigDecimal.valueOf(80));
        }
        if (StringUtils.isEmpty(scResource.getSummary()) && StringUtils.isNotEmpty(scResource.getContentText())) {
            String content = scResource.getContentText().trim();
            scResource.setSummary(content.length() > 120 ? content.substring(0, 120) + "..." : content);
        }
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
                + safeCount(resource.getFavoriteCount()));
        vo.setSummary(StringUtils.defaultIfEmpty(resource.getSummary(), "建议补充资源摘要，便于推荐与画像解释"));
        vo.setRecommendationReason(buildResourceReason(resource));
        return vo;
    }

    private Map<String, Integer> buildTypeDistribution(List<ScResource> resources) {
        Map<String, Integer> distribution = new LinkedHashMap<>();
        for (ScResource resource : resources) {
            String key = StringUtils.defaultIfEmpty(resource.getResourceType(), "unknown");
            distribution.put(key, distribution.getOrDefault(key, 0) + 1);
        }
        return distribution;
    }

    private BigDecimal calculateHeatScore(ScResource resource) {
        BigDecimal quality = defaultQuality(resource.getQualityScore());
        BigDecimal interactionScore = BigDecimal.valueOf(safeCount(resource.getViewCount()) * 0.4
                + safeCount(resource.getDownloadCount()) * 1.2
                + safeCount(resource.getFavoriteCount()) * 1.8);
        return quality.multiply(BigDecimal.valueOf(0.55))
                .add(interactionScore.min(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(0.45)))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRecommendationPotential(ScResource resource) {
        BigDecimal base = calculateHeatScore(resource).multiply(BigDecimal.valueOf(0.6))
                .add(defaultQuality(resource.getQualityScore()).multiply(BigDecimal.valueOf(0.4)));
        if ("video".equalsIgnoreCase(resource.getResourceType())) {
            base = base.add(BigDecimal.valueOf(4));
        } else if ("question".equalsIgnoreCase(resource.getResourceType())) {
            base = base.add(BigDecimal.valueOf(6));
        } else if ("doc".equalsIgnoreCase(resource.getResourceType()) || "pdf".equalsIgnoreCase(resource.getResourceType())) {
            base = base.add(BigDecimal.valueOf(2));
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
            return "资源尚未完成审核，建议先完成审核后进入推荐池";
        }
        if (safeCount(resource.getFavoriteCount()) >= 5) {
            return "收藏表现较好，适合作为高意向资源持续推荐";
        }
        if ("question".equalsIgnoreCase(resource.getResourceType())) {
            return "题目型资源适合错题巩固与阶段练习场景";
        }
        if ("video".equalsIgnoreCase(resource.getResourceType())) {
            return "视频型资源适合首轮学习和进度追赶场景";
        }
        return "建议补齐摘要与内容标签，提升画像匹配与推荐解释性";
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
}
