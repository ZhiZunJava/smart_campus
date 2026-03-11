package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.mapper.ScLearningProfileMapper;
import com.smart.system.mapper.ScStudyRecordMapper;
import com.smart.system.service.IScLearningProfileService;

@Service
public class ScLearningProfileServiceImpl implements IScLearningProfileService
{
    @Autowired
    private ScLearningProfileMapper scLearningProfileMapper;

    @Autowired
    private ScStudyRecordMapper scStudyRecordMapper;

    @Override
    public ScLearningProfile selectScLearningProfileByProfileId(Long profileId) { return scLearningProfileMapper.selectScLearningProfileByProfileId(profileId); }

    @Override
    public List<ScLearningProfile> selectScLearningProfileList(ScLearningProfile scLearningProfile) { return scLearningProfileMapper.selectScLearningProfileList(scLearningProfile); }

    @Override
    public int insertScLearningProfile(ScLearningProfile scLearningProfile) { return scLearningProfileMapper.insertScLearningProfile(scLearningProfile); }

    @Override
    public int updateScLearningProfile(ScLearningProfile scLearningProfile) { return scLearningProfileMapper.updateScLearningProfile(scLearningProfile); }

    @Override
    public int deleteScLearningProfileByProfileIds(Long[] profileIds) { return scLearningProfileMapper.deleteScLearningProfileByProfileIds(profileIds); }

    @Override
    public int deleteScLearningProfileByProfileId(Long profileId) { return scLearningProfileMapper.deleteScLearningProfileByProfileId(profileId); }

    @Override
    public ScLearningProfile rebuildProfile(Long userId, Long courseId)
    {
        ScStudyRecord query = new ScStudyRecord();
        query.setUserId(userId);
        query.setCourseId(courseId);
        List<ScStudyRecord> records = scStudyRecordMapper.selectScStudyRecordList(query);

        int totalCount = records.size();
        int totalDuration = 0;
        BigDecimal totalProgress = BigDecimal.ZERO;
        int resourceCount = 0;

        for (ScStudyRecord record : records)
        {
            totalDuration += record.getDurationSeconds() == null ? 0 : record.getDurationSeconds();
            totalProgress = totalProgress.add(record.getProgressRate() == null ? BigDecimal.ZERO : record.getProgressRate());
            if (record.getResourceId() != null)
            {
                resourceCount++;
            }
        }

        BigDecimal activeScore = BigDecimal.valueOf(Math.min(totalCount * 10, 100));
        BigDecimal concentrationScore = BigDecimal.valueOf(Math.min(totalDuration / 18.0, 100)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal masteryScore = totalCount == 0 ? BigDecimal.ZERO : totalProgress.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
        BigDecimal interestScore = BigDecimal.valueOf(Math.min(resourceCount * 12, 100));
        BigDecimal riskScore = BigDecimal.valueOf(100).subtract(activeScore.multiply(BigDecimal.valueOf(0.4)).add(masteryScore.multiply(BigDecimal.valueOf(0.6)))).setScale(2, RoundingMode.HALF_UP);

        String abilityLevel;
        if (masteryScore.compareTo(BigDecimal.valueOf(85)) >= 0)
        {
            abilityLevel = "HIGH";
        }
        else if (masteryScore.compareTo(BigDecimal.valueOf(60)) >= 0)
        {
            abilityLevel = "MEDIUM";
        }
        else
        {
            abilityLevel = "LOW";
        }

        ScLearningProfile existsQuery = new ScLearningProfile();
        existsQuery.setUserId(userId);
        existsQuery.setCourseId(courseId);
        List<ScLearningProfile> existsList = scLearningProfileMapper.selectScLearningProfileList(existsQuery);

        ScLearningProfile profile = existsList.isEmpty() ? new ScLearningProfile() : existsList.get(0);
        profile.setUserId(userId);
        profile.setCourseId(courseId);
        profile.setAbilityLevel(abilityLevel);
        profile.setActiveScore(activeScore.setScale(2, RoundingMode.HALF_UP));
        profile.setConcentrationScore(concentrationScore);
        profile.setMasteryScore(masteryScore);
        profile.setInterestScore(interestScore.setScale(2, RoundingMode.HALF_UP));
        profile.setRiskScore(riskScore.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : riskScore);
        profile.setProfileVersion(profile.getProfileVersion() == null ? 1 : profile.getProfileVersion() + 1);
        profile.setFeatureJson(buildFeatureJson(totalCount, totalDuration, resourceCount, masteryScore));
        profile.setLastCalculatedTime(new Date());

        if (profile.getProfileId() == null)
        {
            scLearningProfileMapper.insertScLearningProfile(profile);
        }
        else
        {
            scLearningProfileMapper.updateScLearningProfile(profile);
        }
        return profile;
    }

    private String buildFeatureJson(int totalCount, int totalDuration, int resourceCount, BigDecimal masteryScore)
    {
        return String.format("{\"behaviorCount\":%d,\"durationSeconds\":%d,\"resourceCount\":%d,\"masteryScore\":%s}",
            totalCount, totalDuration, resourceCount, masteryScore.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }
}
