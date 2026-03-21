package com.smart.system.domain.campusvo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ResourceOperationOverviewVo {
    private Integer totalResourceCount;
    private Integer publishedResourceCount;
    private Integer pendingAuditCount;
    private Integer highPotentialCount;
    private BigDecimal avgQualityScore;
    private BigDecimal avgHeatScore;
    private Map<String, Integer> resourceTypeDistribution;
    private List<ResourceInsightVo> hottestResources;
    private List<ResourceInsightVo> recommendedResources;

    public Integer getTotalResourceCount() {
        return totalResourceCount;
    }

    public void setTotalResourceCount(Integer totalResourceCount) {
        this.totalResourceCount = totalResourceCount;
    }

    public Integer getPublishedResourceCount() {
        return publishedResourceCount;
    }

    public void setPublishedResourceCount(Integer publishedResourceCount) {
        this.publishedResourceCount = publishedResourceCount;
    }

    public Integer getPendingAuditCount() {
        return pendingAuditCount;
    }

    public void setPendingAuditCount(Integer pendingAuditCount) {
        this.pendingAuditCount = pendingAuditCount;
    }

    public Integer getHighPotentialCount() {
        return highPotentialCount;
    }

    public void setHighPotentialCount(Integer highPotentialCount) {
        this.highPotentialCount = highPotentialCount;
    }

    public BigDecimal getAvgQualityScore() {
        return avgQualityScore;
    }

    public void setAvgQualityScore(BigDecimal avgQualityScore) {
        this.avgQualityScore = avgQualityScore;
    }

    public BigDecimal getAvgHeatScore() {
        return avgHeatScore;
    }

    public void setAvgHeatScore(BigDecimal avgHeatScore) {
        this.avgHeatScore = avgHeatScore;
    }

    public Map<String, Integer> getResourceTypeDistribution() {
        return resourceTypeDistribution;
    }

    public void setResourceTypeDistribution(Map<String, Integer> resourceTypeDistribution) {
        this.resourceTypeDistribution = resourceTypeDistribution;
    }

    public List<ResourceInsightVo> getHottestResources() {
        return hottestResources;
    }

    public void setHottestResources(List<ResourceInsightVo> hottestResources) {
        this.hottestResources = hottestResources;
    }

    public List<ResourceInsightVo> getRecommendedResources() {
        return recommendedResources;
    }

    public void setRecommendedResources(List<ResourceInsightVo> recommendedResources) {
        this.recommendedResources = recommendedResources;
    }
}
