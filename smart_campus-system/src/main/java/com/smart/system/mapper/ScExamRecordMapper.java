package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamRecord;

public interface ScExamRecordMapper {
    ScExamRecord selectScExamRecordByRecordId(Long recordId);

    List<ScExamRecord> selectScExamRecordList(ScExamRecord scExamRecord);

    int insertScExamRecord(ScExamRecord scExamRecord);

    int updateScExamRecord(ScExamRecord scExamRecord);

    int deleteScExamRecordByRecordId(Long recordId);

    int deleteScExamRecordByRecordIds(Long[] recordIds);
}
