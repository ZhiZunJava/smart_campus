package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScStudyRecord;

public interface IScStudyRecordService
{
    ScStudyRecord selectScStudyRecordByRecordId(Long recordId);
    List<ScStudyRecord> selectScStudyRecordList(ScStudyRecord scStudyRecord);
    int insertScStudyRecord(ScStudyRecord scStudyRecord);
    int updateScStudyRecord(ScStudyRecord scStudyRecord);
    int deleteScStudyRecordByRecordIds(Long[] recordIds);
    int deleteScStudyRecordByRecordId(Long recordId);
    int recordBehavior(ScStudyRecord scStudyRecord);
}
