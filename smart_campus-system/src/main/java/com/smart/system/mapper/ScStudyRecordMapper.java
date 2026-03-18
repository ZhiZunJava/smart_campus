package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScStudyRecord;

public interface ScStudyRecordMapper {
    ScStudyRecord selectScStudyRecordByRecordId(Long recordId);

    List<ScStudyRecord> selectScStudyRecordList(ScStudyRecord scStudyRecord);

    int insertScStudyRecord(ScStudyRecord scStudyRecord);

    int updateScStudyRecord(ScStudyRecord scStudyRecord);

    int deleteScStudyRecordByRecordId(Long recordId);

    int deleteScStudyRecordByRecordIds(Long[] recordIds);
}
