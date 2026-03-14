package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScLearningProfile;
import com.smart.system.domain.ScLearningWarning;
import com.smart.system.mapper.ScLearningProfileMapper;
import com.smart.system.mapper.ScLearningWarningMapper;
import com.smart.system.service.IScLearningWarningService;

@Service
public class ScLearningWarningServiceImpl implements IScLearningWarningService
{
    @Autowired
    private ScLearningWarningMapper scLearningWarningMapper;
    @Autowired
    private ScLearningProfileMapper scLearningProfileMapper;

    public ScLearningWarning selectScLearningWarningByWarningId(Long warningId) { return scLearningWarningMapper.selectScLearningWarningByWarningId(warningId); }
    public List<ScLearningWarning> selectScLearningWarningList(ScLearningWarning scLearningWarning) { return scLearningWarningMapper.selectScLearningWarningList(scLearningWarning); }
    public int insertScLearningWarning(ScLearningWarning scLearningWarning) { return scLearningWarningMapper.insertScLearningWarning(scLearningWarning); }
    public int updateScLearningWarning(ScLearningWarning scLearningWarning) { return scLearningWarningMapper.updateScLearningWarning(scLearningWarning); }
    public int deleteScLearningWarningByWarningIds(Long[] warningIds) { return scLearningWarningMapper.deleteScLearningWarningByWarningIds(warningIds); }
    public int deleteScLearningWarningByWarningId(Long warningId) { return scLearningWarningMapper.deleteScLearningWarningByWarningId(warningId); }

    public ScLearningWarning buildWarning(Long userId, Long courseId)
    {
        ScLearningProfile query = new ScLearningProfile();
        query.setUserId(userId);
        query.setCourseId(courseId);
        List<ScLearningProfile> profiles = scLearningProfileMapper.selectScLearningProfileList(query);
        if (profiles.isEmpty())
        {
            return null;
        }
        ScLearningProfile profile = profiles.get(0);
        if (profile.getRiskScore() == null || profile.getRiskScore().compareTo(BigDecimal.valueOf(40)) < 0)
        {
            return null;
        }

        String warningType = profile.getRiskScore().compareTo(BigDecimal.valueOf(70)) >= 0 ? "high_risk" : "low_active";
        ScLearningWarning warningQuery = new ScLearningWarning();
        warningQuery.setUserId(userId);
        warningQuery.setCourseId(courseId);
        warningQuery.setWarningType(warningType);
        warningQuery.setProcessStatus("PENDING");
        List<ScLearningWarning> existingWarnings = scLearningWarningMapper.selectScLearningWarningList(warningQuery);
        if (!existingWarnings.isEmpty())
        {
            ScLearningWarning existingWarning = existingWarnings.get(0);
            existingWarning.setWarningLevel(profile.getRiskScore().compareTo(BigDecimal.valueOf(70)) >= 0 ? "HIGH" : "MEDIUM");
            existingWarning.setWarningContent(buildContent(profile.getRiskScore()));
            existingWarning.setSuggestion(buildSuggestion(profile.getRiskScore()));
            existingWarning.setProcessTime(null);
            scLearningWarningMapper.updateScLearningWarning(existingWarning);
            return existingWarning;
        }

        ScLearningWarning warning = new ScLearningWarning();
        warning.setUserId(userId);
        warning.setCourseId(courseId);
        warning.setWarningType(warningType);
        warning.setWarningLevel(profile.getRiskScore().compareTo(BigDecimal.valueOf(70)) >= 0 ? "HIGH" : "MEDIUM");
        warning.setWarningContent(buildContent(profile.getRiskScore()));
        warning.setSuggestion(buildSuggestion(profile.getRiskScore()));
        warning.setProcessStatus("PENDING");
        warning.setSourceType("rule");
        scLearningWarningMapper.insertScLearningWarning(warning);
        return warning;
    }

    @Override
    public int processWarning(Long warningId, String processStatus)
    {
        ScLearningWarning warning = scLearningWarningMapper.selectScLearningWarningByWarningId(warningId);
        if (warning == null)
        {
            return 0;
        }
        warning.setProcessStatus(processStatus == null || processStatus.trim().isEmpty() ? "PROCESSED" : processStatus.trim());
        warning.setProcessTime(new Date());
        return scLearningWarningMapper.updateScLearningWarning(warning);
    }

    private String buildContent(BigDecimal riskScore)
    {
        if (riskScore.compareTo(BigDecimal.valueOf(70)) >= 0)
        {
            return "近期学习活跃度与掌握度偏低，存在较高学习风险";
        }
        return "近期学习行为减少，建议及时跟进学习状态";
    }

    private String buildSuggestion(BigDecimal riskScore)
    {
        if (riskScore.compareTo(BigDecimal.valueOf(70)) >= 0)
        {
            return "建议教师或家长及时干预，补充重点资源并安排阶段性测验";
        }
        return "建议补充学习计划，连续完成课程学习与巩固练习";
    }
}
