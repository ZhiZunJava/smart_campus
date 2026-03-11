package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScExamRecord;

public interface IScExamRecordService
{
    ScExamRecord selectScExamRecordByRecordId(Long recordId);
    List<ScExamRecord> selectScExamRecordList(ScExamRecord scExamRecord);
    int insertScExamRecord(ScExamRecord scExamRecord);
    int updateScExamRecord(ScExamRecord scExamRecord);
    int deleteScExamRecordByRecordIds(Long[] recordIds);
    int deleteScExamRecordByRecordId(Long recordId);
    ScExamRecord startExam(Long paperId, Long userId);
    ScExamRecord submitExam(Long recordId, List<Map<String, Object>> answers);
}
