package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.mapper.ScExamPaperMapper;
import com.smart.system.mapper.ScExamRecordMapper;
import com.smart.system.mapper.ScLearningProfileMapper;
import com.smart.system.mapper.ScStudyRecordMapper;
import com.smart.system.mapper.ScWrongQuestionBookMapper;
import com.smart.system.service.IScLearningProfileService;

@Service
public class ScLearningProfileServiceImpl implements IScLearningProfileService {
    @Autowired
    private ScLearningProfileMapper scLearningProfileMapper;

    @Autowired
    private ScStudyRecordMapper scStudyRecordMapper;

    @Autowired
    private ScExamRecordMapper scExamRecordMapper;

    @Autowired
    private ScExamPaperMapper scExamPaperMapper;

    @Autowired
    private ScWrongQuestionBookMapper scWrongQuestionBookMapper;

    @Override
    public ScLearningProfile selectScLearningProfileByProfileId(Long profileId) {
        return scLearningProfileMapper.selectScLearningProfileByProfileId(profileId);
    }

    @Override
    public List<ScLearningProfile> selectScLearningProfileList(ScLearningProfile scLearningProfile) {
        return scLearningProfileMapper.selectScLearningProfileList(scLearningProfile);
    }

    @Override
    public int insertScLearningProfile(ScLearningProfile scLearningProfile) {
        return scLearningProfileMapper.insertScLearningProfile(scLearningProfile);
    }

    @Override
    public int updateScLearningProfile(ScLearningProfile scLearningProfile) {
        return scLearningProfileMapper.updateScLearningProfile(scLearningProfile);
    }

    @Override
    public int deleteScLearningProfileByProfileIds(Long[] profileIds) {
        return scLearningProfileMapper.deleteScLearningProfileByProfileIds(profileIds);
    }

    @Override
    public int deleteScLearningProfileByProfileId(Long profileId) {
        return scLearningProfileMapper.deleteScLearningProfileByProfileId(profileId);
    }

    @Override
    public ScLearningProfile rebuildProfile(Long userId, Long courseId) {
        ScStudyRecord query = new ScStudyRecord();
        query.setUserId(userId);
        query.setCourseId(courseId);
        List<ScStudyRecord> records = scStudyRecordMapper.selectScStudyRecordList(query);

        Long resolvedCourseId = courseId;
        if (resolvedCourseId == null) {
            for (ScStudyRecord record : records) {
                if (record.getCourseId() != null) {
                    resolvedCourseId = record.getCourseId();
                    break;
                }
            }
        }

        if (resolvedCourseId == null) {
            return null;
        }

        int totalCount = records.size();
        int totalDuration = 0;
        BigDecimal totalProgress = BigDecimal.ZERO;
        Set<Long> resourceIds = new LinkedHashSet<>();
        Set<String> studyDays = new LinkedHashSet<>();
        Map<String, Integer> behaviorTypeStats = new LinkedHashMap<>();
        Map<String, Integer> deviceTypeStats = new LinkedHashMap<>();

        for (ScStudyRecord record : records) {
            totalDuration += record.getDurationSeconds() == null ? 0 : record.getDurationSeconds();
            totalProgress = totalProgress
                    .add(record.getProgressRate() == null ? BigDecimal.ZERO : record.getProgressRate());
            if (record.getResourceId() != null) {
                resourceIds.add(record.getResourceId());
            }
            if (record.getCreateTime() != null) {
                studyDays.add(new SimpleDateFormat("yyyy-MM-dd").format(record.getCreateTime()));
            }
            countMap(behaviorTypeStats, StringUtils.defaultIfEmpty(record.getBehaviorType(), "unknown"));
            countMap(deviceTypeStats, StringUtils.defaultIfEmpty(record.getDeviceType(), "unknown"));
        }

        List<ScExamRecord> examRecords = listExamRecords(userId, resolvedCourseId);
        List<ScWrongQuestionBook> wrongQuestions = listWrongQuestions(userId, resolvedCourseId);
        BigDecimal avgExamScore = avgExamScore(examRecords);
        BigDecimal avgCorrectRate = avgCorrectRate(examRecords);
        int unresolvedWrongCount = (int) wrongQuestions.stream()
                .filter(item -> !"MASTERED".equalsIgnoreCase(item.getMasteryStatus()))
                .count();
        int completedBehaviorCount = behaviorTypeStats.getOrDefault("complete", 0)
                + behaviorTypeStats.getOrDefault("submit", 0);

        BigDecimal activeScore = BigDecimal.valueOf(Math.min(totalCount * 8 + studyDays.size() * 6, 100))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal concentrationScore = BigDecimal.valueOf(Math.min(totalDuration / 15.0, 100))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal progressScore = totalCount == 0 ? BigDecimal.ZERO
                : totalProgress.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
        BigDecimal masteryScore = progressScore.multiply(BigDecimal.valueOf(0.65))
                .add(avgCorrectRate.multiply(BigDecimal.valueOf(0.35)))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal interestScore = BigDecimal
                .valueOf(Math.min(resourceIds.size() * 10 + behaviorTypeStats.size() * 6, 100))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal riskScore = BigDecimal.valueOf(100).subtract(
                activeScore.multiply(BigDecimal.valueOf(0.25))
                        .add(concentrationScore.multiply(BigDecimal.valueOf(0.15)))
                        .add(masteryScore.multiply(BigDecimal.valueOf(0.4)))
                        .add(interestScore.multiply(BigDecimal.valueOf(0.2))))
                .add(BigDecimal.valueOf(Math.min(unresolvedWrongCount * 2L, 15)))
                .setScale(2, RoundingMode.HALF_UP);
        if (avgExamScore.compareTo(BigDecimal.ZERO) > 0 && avgExamScore.compareTo(BigDecimal.valueOf(60)) < 0) {
            riskScore = riskScore.add(BigDecimal.valueOf(8));
        }
        if (studyDays.size() <= 1 && totalCount > 0) {
            riskScore = riskScore.add(BigDecimal.valueOf(5));
        }
        riskScore = riskScore.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));

        String abilityLevel;
        if (masteryScore.compareTo(BigDecimal.valueOf(85)) >= 0) {
            abilityLevel = "HIGH";
        } else if (masteryScore.compareTo(BigDecimal.valueOf(60)) >= 0) {
            abilityLevel = "MEDIUM";
        } else {
            abilityLevel = "LOW";
        }

        ScLearningProfile existsQuery = new ScLearningProfile();
        existsQuery.setUserId(userId);
        existsQuery.setCourseId(resolvedCourseId);
        List<ScLearningProfile> existsList = scLearningProfileMapper.selectScLearningProfileList(existsQuery);

        ScLearningProfile profile = existsList.isEmpty() ? new ScLearningProfile() : existsList.get(0);
        profile.setUserId(userId);
        profile.setCourseId(resolvedCourseId);
        profile.setAbilityLevel(abilityLevel);
        profile.setActiveScore(activeScore);
        profile.setConcentrationScore(concentrationScore);
        profile.setMasteryScore(masteryScore);
        profile.setInterestScore(interestScore);
        profile.setRiskScore(riskScore);
        profile.setProfileVersion(profile.getProfileVersion() == null ? 1 : profile.getProfileVersion() + 1);
        profile.setFeatureJson(buildFeatureJson(totalCount, totalDuration, resourceIds.size(), studyDays.size(),
                progressScore, avgExamScore, avgCorrectRate, unresolvedWrongCount, completedBehaviorCount,
                behaviorTypeStats, deviceTypeStats));
        profile.setLastCalculatedTime(new Date());

        if (profile.getProfileId() == null) {
            scLearningProfileMapper.insertScLearningProfile(profile);
        } else {
            scLearningProfileMapper.updateScLearningProfile(profile);
        }
        return profile;
    }

    private List<ScExamRecord> listExamRecords(Long userId, Long courseId) {
        ScExamRecord query = new ScExamRecord();
        query.setUserId(userId);
        List<ScExamRecord> records = scExamRecordMapper.selectScExamRecordList(query);
        if (courseId == null) {
            return records;
        }
        return records.stream().filter(record -> matchCourse(record, courseId)).toList();
    }

    private boolean matchCourse(ScExamRecord record, Long courseId) {
        if (record == null || record.getPaperId() == null) {
            return false;
        }
        ScExamPaper paper = scExamPaperMapper.selectScExamPaperByPaperId(record.getPaperId());
        return paper != null && courseId.equals(paper.getCourseId());
    }

    private List<ScWrongQuestionBook> listWrongQuestions(Long userId, Long courseId) {
        ScWrongQuestionBook query = new ScWrongQuestionBook();
        query.setUserId(userId);
        query.setCourseId(courseId);
        return scWrongQuestionBookMapper.selectScWrongQuestionBookList(query);
    }

    private BigDecimal avgExamScore(List<ScExamRecord> examRecords) {
        return average(examRecords.stream()
                .map(ScExamRecord::getScore)
                .filter(item -> item != null)
                .toList());
    }

    private BigDecimal avgCorrectRate(List<ScExamRecord> examRecords) {
        return average(examRecords.stream()
                .map(ScExamRecord::getCorrectRate)
                .filter(item -> item != null)
                .toList());
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            total = total.add(value);
        }
        return total.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }

    private void countMap(Map<String, Integer> statMap, String key) {
        statMap.put(key, statMap.getOrDefault(key, 0) + 1);
    }

    private String buildFeatureJson(int totalCount, int totalDuration, int resourceCount, int studyDayCount,
            BigDecimal progressScore, BigDecimal avgExamScore, BigDecimal avgCorrectRate, int wrongQuestionCount,
            int completedBehaviorCount, Map<String, Integer> behaviorTypeStats, Map<String, Integer> deviceTypeStats) {
        return String.format(
                "{\"behaviorCount\":%d,\"durationSeconds\":%d,\"resourceCount\":%d,\"studyDayCount\":%d,\"progressScore\":%s,\"avgExamScore\":%s,\"avgCorrectRate\":%s,\"wrongQuestionCount\":%d,\"completedBehaviorCount\":%d,\"behaviorTypes\":\"%s\",\"deviceTypes\":\"%s\"}",
                totalCount,
                totalDuration,
                resourceCount,
                studyDayCount,
                progressScore.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                avgExamScore.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                avgCorrectRate.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                wrongQuestionCount,
                completedBehaviorCount,
                behaviorTypeStats.toString().replace("\"", "'"),
                deviceTypeStats.toString().replace("\"", "'"));
    }
}
