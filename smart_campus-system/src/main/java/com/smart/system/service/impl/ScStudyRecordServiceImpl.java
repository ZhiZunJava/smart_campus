package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.mapper.ScResourceMapper;
import com.smart.system.mapper.ScStudyRecordMapper;
import com.smart.system.service.IScLearningProfileService;
import com.smart.system.service.IScLearningRecommendationService;
import com.smart.system.service.IScLearningWarningService;
import com.smart.system.service.IScStudyRecordService;

@Service
public class ScStudyRecordServiceImpl implements IScStudyRecordService {
    @Autowired
    private ScStudyRecordMapper scStudyRecordMapper;

    @Autowired
    private ScResourceMapper scResourceMapper;

    @Autowired
    private IScLearningProfileService scLearningProfileService;

    @Autowired
    private IScLearningWarningService scLearningWarningService;

    @Autowired
    private IScLearningRecommendationService scLearningRecommendationService;

    @Override
    public ScStudyRecord selectScStudyRecordByRecordId(Long recordId) {
        return scStudyRecordMapper.selectScStudyRecordByRecordId(recordId);
    }

    @Override
    public List<ScStudyRecord> selectScStudyRecordList(ScStudyRecord scStudyRecord) {
        return scStudyRecordMapper.selectScStudyRecordList(scStudyRecord);
    }

    @Override
    public int insertScStudyRecord(ScStudyRecord scStudyRecord) {
        return scStudyRecordMapper.insertScStudyRecord(scStudyRecord);
    }

    @Override
    public int updateScStudyRecord(ScStudyRecord scStudyRecord) {
        return scStudyRecordMapper.updateScStudyRecord(scStudyRecord);
    }

    @Override
    public int deleteScStudyRecordByRecordIds(Long[] recordIds) {
        return scStudyRecordMapper.deleteScStudyRecordByRecordIds(recordIds);
    }

    @Override
    public int deleteScStudyRecordByRecordId(Long recordId) {
        return scStudyRecordMapper.deleteScStudyRecordByRecordId(recordId);
    }

    @Override
    public int recordBehavior(ScStudyRecord scStudyRecord) {
        normalizeStudyRecord(scStudyRecord);
        if (scStudyRecord.getDurationSeconds() == null) {
            scStudyRecord.setDurationSeconds(0);
        }
        if (scStudyRecord.getProgressRate() == null) {
            scStudyRecord.setProgressRate(BigDecimal.ZERO);
        }
        int rows = scStudyRecordMapper.insertScStudyRecord(scStudyRecord);
        if (rows > 0 && scStudyRecord.getResourceId() != null
                && StringUtils.isNotEmpty(scStudyRecord.getBehaviorType())) {
            String behaviorType = scStudyRecord.getBehaviorType();
            if ("view".equalsIgnoreCase(behaviorType) || "play".equalsIgnoreCase(behaviorType)) {
                scResourceMapper.increaseViewCount(scStudyRecord.getResourceId());
            } else if ("download".equalsIgnoreCase(behaviorType)) {
                scResourceMapper.increaseDownloadCount(scStudyRecord.getResourceId());
            } else if ("favorite".equalsIgnoreCase(behaviorType)) {
                scResourceMapper.increaseFavoriteCount(scStudyRecord.getResourceId());
            }
        }
        if (rows > 0 && scStudyRecord.getUserId() != null) {
            scLearningProfileService.rebuildProfile(scStudyRecord.getUserId(), scStudyRecord.getCourseId());
            scLearningWarningService.buildWarning(scStudyRecord.getUserId(), scStudyRecord.getCourseId());
            scLearningRecommendationService.generateRecommendations(scStudyRecord.getUserId(), "home", 5);
            scLearningRecommendationService.generateRecommendations(scStudyRecord.getUserId(), "workbench", 5);
            if (scStudyRecord.getCourseId() != null) {
                scLearningRecommendationService.generateRecommendations(scStudyRecord.getUserId(), "course", 5);
            }
        }
        return rows;
    }

    private void normalizeStudyRecord(ScStudyRecord scStudyRecord) {
        if (scStudyRecord == null) {
            return;
        }
        if (scStudyRecord.getProgressRate() != null) {
            if (scStudyRecord.getProgressRate().compareTo(BigDecimal.ZERO) < 0) {
                scStudyRecord.setProgressRate(BigDecimal.ZERO);
            } else if (scStudyRecord.getProgressRate().compareTo(BigDecimal.valueOf(100)) > 0) {
                scStudyRecord.setProgressRate(BigDecimal.valueOf(100));
            }
        }
        if (StringUtils.isEmpty(scStudyRecord.getDeviceType())) {
            scStudyRecord.setDeviceType("web");
        }
        if (StringUtils.isEmpty(scStudyRecord.getSourcePage())) {
            scStudyRecord.setSourcePage("learning_center");
        }
        if (scStudyRecord.getCourseId() == null && scStudyRecord.getResourceId() != null) {
            ScResource resource = scResourceMapper.selectScResourceByResourceId(scStudyRecord.getResourceId());
            if (resource != null) {
                scStudyRecord.setCourseId(resource.getCourseId());
                if (scStudyRecord.getChapterId() == null) {
                    scStudyRecord.setChapterId(resource.getChapterId());
                }
            }
        }
    }
}
