package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.domain.ScLearningRecommendation;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.domain.dto.RecommendationFeedbackDto;
import com.smart.system.mapper.ScLearningProfileMapper;
import com.smart.system.mapper.ScLearningRecommendationMapper;
import com.smart.system.mapper.ScResourceMapper;
import com.smart.system.mapper.ScStudyRecordMapper;
import com.smart.system.mapper.ScWrongQuestionBookMapper;
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

    @Autowired
    private ScStudyRecordMapper scStudyRecordMapper;

    @Autowired
    private ScWrongQuestionBookMapper scWrongQuestionBookMapper;

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
        String resolvedSceneCode = StringUtils.isEmpty(sceneCode) ? "home" : sceneCode;

        ScLearningRecommendation recommendationQuery = new ScLearningRecommendation();
        recommendationQuery.setUserId(userId);
        recommendationQuery.setSceneCode(resolvedSceneCode);
        List<ScLearningRecommendation> existingRecommendations = scLearningRecommendationMapper.selectScLearningRecommendationList(recommendationQuery);
        List<ScLearningRecommendation> validRecommendations = new ArrayList<>();
        Date now = new Date();
        Set<Long> existingBizIds = new HashSet<>();
        for (ScLearningRecommendation existingRecommendation : existingRecommendations)
        {
            if (existingRecommendation.getExpireTime() == null || existingRecommendation.getExpireTime().after(now))
            {
                validRecommendations.add(existingRecommendation);
                if (existingRecommendation.getBizId() != null)
                {
                    existingBizIds.add(existingRecommendation.getBizId());
                }
            }
        }
        validRecommendations.sort((left, right) -> safeScore(right).compareTo(safeScore(left)));
        if (validRecommendations.size() >= size)
        {
            return new ArrayList<>(validRecommendations.subList(0, size));
        }

        ScLearningProfile profileQuery = new ScLearningProfile();
        profileQuery.setUserId(userId);
        List<ScLearningProfile> profiles = scLearningProfileMapper.selectScLearningProfileList(profileQuery);
        Long courseId = profiles.isEmpty() ? null : profiles.get(0).getCourseId();
        BigDecimal riskScore = profiles.isEmpty() || profiles.get(0).getRiskScore() == null
            ? BigDecimal.ZERO
            : profiles.get(0).getRiskScore();

        ScStudyRecord studyRecordQuery = new ScStudyRecord();
        studyRecordQuery.setUserId(userId);
        studyRecordQuery.setCourseId(courseId);
        List<ScStudyRecord> studyRecords = scStudyRecordMapper.selectScStudyRecordList(studyRecordQuery);
        Set<Long> learnedResourceIds = new HashSet<>();
        for (ScStudyRecord studyRecord : studyRecords)
        {
            if (studyRecord.getResourceId() != null)
            {
                learnedResourceIds.add(studyRecord.getResourceId());
            }
        }

        ScWrongQuestionBook wrongQuestionBookQuery = new ScWrongQuestionBook();
        wrongQuestionBookQuery.setUserId(userId);
        wrongQuestionBookQuery.setCourseId(courseId);
        int wrongQuestionCount = scWrongQuestionBookMapper.selectScWrongQuestionBookList(wrongQuestionBookQuery).size();

        ScResource resourceQuery = new ScResource();
        resourceQuery.setStatus("0");
        resourceQuery.setAuditStatus("1");
        resourceQuery.setCourseId(courseId);
        List<ScResource> resources = scResourceMapper.selectScResourceList(resourceQuery);
        List<ScLearningRecommendation> results = new ArrayList<>(validRecommendations);

        int counter = validRecommendations.size();
        for (ScResource resource : resources)
        {
            if (counter >= size)
            {
                break;
            }
            if (resource.getResourceId() == null || existingBizIds.contains(resource.getResourceId()))
            {
                continue;
            }
            ScLearningRecommendation recommendation = new ScLearningRecommendation();
            recommendation.setUserId(userId);
            recommendation.setBizType("resource");
            recommendation.setBizId(resource.getResourceId());
            recommendation.setSourceType("rule");
            recommendation.setSceneCode(resolvedSceneCode);
            recommendation.setExposeStatus("0");
            recommendation.setClickStatus("0");
            recommendation.setFeedbackStatus("0");
            recommendation.setExpireTime(new Date(System.currentTimeMillis() + 7L * 24 * 3600 * 1000));
            recommendation.setRecommendReason(buildReason(resource.getResourceType(), riskScore, learnedResourceIds.contains(resource.getResourceId()), wrongQuestionCount));
            recommendation.setRecommendScore(buildScore(resource, riskScore, counter, learnedResourceIds.contains(resource.getResourceId()), wrongQuestionCount));
            recommendation.setCreateBy("system");
            scLearningRecommendationMapper.insertScLearningRecommendation(recommendation);
            results.add(recommendation);
            existingBizIds.add(resource.getResourceId());
            counter++;
        }
        results.sort((left, right) -> safeScore(right).compareTo(safeScore(left)));
        return results;
    }

    @Override
    public List<ScLearningRecommendation> listActiveRecommendations(Long userId, String sceneCode, Integer limit)
    {
        int size = (limit == null || limit <= 0) ? 5 : limit;
        String resolvedSceneCode = StringUtils.isEmpty(sceneCode) ? "home" : sceneCode;
        ScLearningRecommendation query = new ScLearningRecommendation();
        query.setUserId(userId);
        query.setSceneCode(resolvedSceneCode);
        Date now = new Date();
        List<ScLearningRecommendation> recommendations = scLearningRecommendationMapper.selectScLearningRecommendationList(query).stream()
            .filter(item -> item.getExpireTime() == null || item.getExpireTime().after(now))
            .sorted((left, right) -> safeScore(right).compareTo(safeScore(left)))
            .limit(size)
            .toList();
        if (!recommendations.isEmpty())
        {
            return recommendations;
        }
        return generateRecommendations(userId, resolvedSceneCode, size);
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

    private String buildReason(String resourceType, BigDecimal riskScore, boolean learned, int wrongQuestionCount)
    {
        if (riskScore.compareTo(BigDecimal.valueOf(60)) >= 0)
        {
            return "基于近期学习活跃度偏低，优先推荐高质量资源进行巩固";
        }
        if (wrongQuestionCount >= 3)
        {
            return "基于近期错题积累情况，优先推荐适合查漏补缺的课程资源";
        }
        if (!learned)
        {
            return "基于当前学习进度，优先推荐尚未充分学习的新资源";
        }
        if ("video".equalsIgnoreCase(resourceType))
        {
            return "基于你的课程学习进度，优先推荐适合当前阶段的视频资源";
        }
        return "基于课程关联与资源质量评分生成推荐";
    }

    private BigDecimal buildScore(ScResource resource, BigDecimal riskScore, int rankOffset, boolean learned, int wrongQuestionCount)
    {
        BigDecimal quality = resource.getQualityScore() == null ? BigDecimal.valueOf(60) : resource.getQualityScore();
        BigDecimal normalizedRiskScore = riskScore == null ? BigDecimal.ZERO : riskScore;
        BigDecimal score = quality.multiply(BigDecimal.valueOf(0.7)).add(BigDecimal.valueOf(100).subtract(normalizedRiskScore).multiply(BigDecimal.valueOf(0.3)));
        if (!learned)
        {
            score = score.add(BigDecimal.valueOf(8));
        }
        if (wrongQuestionCount >= 3)
        {
            score = score.add(BigDecimal.valueOf(5));
        }
        if ("video".equalsIgnoreCase(resource.getResourceType()))
        {
            score = score.add(BigDecimal.valueOf(2));
        }
        score = score.subtract(BigDecimal.valueOf(rankOffset * 2L));
        return score.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeScore(ScLearningRecommendation recommendation)
    {
        return recommendation == null || recommendation.getRecommendScore() == null
            ? BigDecimal.ZERO
            : recommendation.getRecommendScore();
    }
}
