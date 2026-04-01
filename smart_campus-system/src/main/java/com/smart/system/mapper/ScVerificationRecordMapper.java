package com.smart.system.mapper;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScVerificationRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 学籍核对记录 Mapper
 *
 * @author can
 */
public interface ScVerificationRecordMapper {
    ScVerificationRecord selectScVerificationRecordByRecordId(Long recordId);

    List<ScVerificationRecord> selectScVerificationRecordList(ScVerificationRecord query);

    ScVerificationRecord selectByBatchAndStudent(@Param("batchId") Long batchId,
                                                  @Param("studentUserId") Long studentUserId);

    List<Map<String, Object>> countByBatchAndStatus(@Param("batchId") Long batchId);

    int insertScVerificationRecord(ScVerificationRecord record);

    int batchInsertRecords(@Param("list") List<ScVerificationRecord> records);

    int updateScVerificationRecord(ScVerificationRecord record);

    int deleteScVerificationRecordByRecordId(Long recordId);

    int deleteScVerificationRecordByBatchId(Long batchId);
}
