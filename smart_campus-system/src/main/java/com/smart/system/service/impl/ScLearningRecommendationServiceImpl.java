package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.domain.ScLearningRecommendation;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.dto.RecommendationFeedbackDto;
import com.smart.system.mapper.ScLearningProfileMapper;
import com.smart.system.mapper.ScLearningRecommendationMapper;
import com.smart.system.mapper.ScResourceMapper;
import com.smart.system.service.IScLearningRecommendationService;

@Service
public class ScLearningRecommendationServiceImpl implements IScLearningRecommendationService
{
    @Autowired
    private ScLearningRecommendationMapper scLearningRecommendationMapper;

    @Autowired
    private ScLearningProfileMapper scLearningProfileMapper;

    @Autowired
    private ScResourceMapper scResourceMapper;

    @Override
    public ScLearningRecommendation selectScLearningRecommendationByRecommendId(Long recommendId) { return scLearningRecommendationMapper.selectScLearningRecommendationByRecommendId(recommendId); }

    @Override
    public List<ScLearningRecommendation> selectScLearningRecommendationList(ScLearningRecommendation scLearningRecommendation) { return scLearningRecommendationMapper.selectScLearningRecommendationList(scLearningRecommendation); }

    @Override
    public int insertScLearningRecommendation(ScLearningRecommendation scLearningRecommendation) { return scLearningRecommendationMapper.insertScLearningRecommendation(scLearningRecommendation); }

    @Override
    public int updateScLearningRecommendation(ScLearningRecommendation scLearningRecommendation) { return scLearningRecommendationMapper.updateScLearningRecommendation(scLearningRecommendation); }

    @Override
    public int deleteScLearningRecommendationByRecommendIds(Long[] recommendIds) { return scLearningRecommendationMapper.deleteScLearningRecommendationByRecommendIds(recommendIds); }

    @Override
    public int deleteScLearningRecommendationByRecommendId(Long recommendId) { return scLearningRecommendationMapper.deleteScLearningRecommendationByRecommendId(recommendId); }

    @Override
    public List<ScLearningRecommendation> generateRecommendations(Long userId, String sceneCode, Integer limit)
    {
        int size = (limit == null || limit <= 0) ? 5 : limit;
        ScLearningProfile profileQuery = new ScLearningProfile();
        profileQuery.setUserId(userId);
        List<ScLearningProfile> profiles = scLearningProfileMapper.selectScLearningProfileList(profileQuery);
        Long courseId = profiles.isEmpty() ? null : profiles.get(0).getCourseId();
        BigDecimal riskScore = profiles.isEmpty() ? BigDecimal.ZERO : profiles.get(0).getRiskScore();

        ScResource resourceQuery = new ScResource();
        resourceQuery.setStatus("0");
        resourceQuery.setAuditStatus("1");
        resourceQuery.setCourseId(courseId);
        List<ScResource> resources = scResourceMapper.selectScResourceList(resourceQuery);
        List<ScLearningRecommendation> results = new ArrayList<>();

        int counter = 0;
        for (ScResource resource : resources)
        {
            if (counter >= size)
            {
                break;
            }
            ScLearningRecommendation recommendation = new ScLearningRecommendation();
            recommendation.setUserId(userId);
            recommendation.setBizType("resource");
            recommendation.setBizId(resource.getResourceId());
            recommendation.setSourceType("rule");
            recommendation.setSceneCode(StringUtils.isEmpty(sceneCode) ? "home" : sceneCode);
            recommendation.setExposeStatus("0");
            recommendation.setClickStatus("0");
            recommendation.setFeedbackStatus("0");
            recommendation.setExpireTime(new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000));
            recommendation.setRecommendReason(buildReason(resource.getResourceType(), riskScore));
            recommendation.setRecommendScore(buildScore(resource.getQualityScore(), riskScore, counter));
            recommendation.setCreateBy("system");
            scLearningRecommendationMapper.insertScLearningRecommendation(recommendation);
            results.add(recommendation);
            counter++;
        }
        return results;
    }

    @Override
    public int feedbackRecommendation(RecommendationFeedbackDto feedbackDto)
    {
        if (feedbackDto == null || feedbackDto.getRecommendId() == null)
        {
            return 0;
        }
        ScLearningRecommendation recommendation = scLearningRecommendationMapper.selectScLearningRecommendationByRecommendId(feedbackDto.getRecommendId());
        if (recommendation == null)
        {
            return 0;
        }
        if (StringUtils.isNotEmpty(feedbackDto.getFeedbackStatus()))
        {
            recommendation.setFeedbackStatus(feedbackDto.getFeedbackStatus());
        }
        if (StringUtils.isNotEmpty(feedbackDto.getClickStatus()))
        {
            recommendation.setClickStatus(feedbackDto.getClickStatus());
        }
        if (StringUtils.isNotEmpty(feedbackDto.getExposeStatus()))
        {
            recommendation.setExposeStatus(feedbackDto.getExposeStatus());
        }
        return scLearningRecommendationMapper.updateScLearningRecommendation(recommendation);
    }

    private String buildReason(String resourceType, BigDecimal riskScore)
    {
        if (riskScore.compareTo(BigDecimal.valueOf(60)) >= 0)
        {
            return "基于近期学习活跃度偏低，优先推荐高质量资源进行巩固";
        }
        if ("video".equalsIgnoreCase(resourceType))
        {
            return "基于你的课程学习进度，优先推荐适合当前阶段的视频资源";
        }
        return "基于课程关联与资源质量评分生成推荐";
    }

    private BigDecimal buildScore(BigDecimal qualityScore, BigDecimal riskScore, int rankOffset)
    {
        BigDecimal quality = qualityScore == null ? BigDecimal.valueOf(60) : qualityScore;
        BigDecimal score = quality.multiply(BigDecimal.valueOf(0.7)).add(BigDecimal.valueOf(100).subtract(riskScore).multiply(BigDecimal.valueOf(0.3)));
        score = score.subtract(BigDecimal.valueOf(rankOffset * 2L));
        return score.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
