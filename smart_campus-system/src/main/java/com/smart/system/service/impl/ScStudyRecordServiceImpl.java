package com.smart.system.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScStudyRecord;
import com.smart.system.mapper.ScResourceMapper;
import com.smart.system.mapper.ScStudyRecordMapper;
import com.smart.system.service.IScStudyRecordService;

@Service
public class ScStudyRecordServiceImpl implements IScStudyRecordService {
    @Autowired
    private ScStudyRecordMapper scStudyRecordMapper;

    @Autowired
    private ScResourceMapper scResourceMapper;

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
        return rows;
    }
}
