package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScVerificationChange;
import org.apache.ibatis.annotations.Param;

/**
 * 学籍核对变更明细 Mapper
 *
 * @author can
 */
public interface ScVerificationChangeMapper {
    ScVerificationChange selectScVerificationChangeByChangeId(Long changeId);

    List<ScVerificationChange> selectByRecordId(@Param("recordId") Long recordId);

    List<ScVerificationChange> selectByBatchAndStudent(@Param("batchId") Long batchId,
                                                        @Param("studentUserId") Long studentUserId);

    int insertScVerificationChange(ScVerificationChange change);

    int batchInsertChanges(@Param("list") List<ScVerificationChange> changes);

    int updateScVerificationChange(ScVerificationChange change);

    int deleteScVerificationChangeByRecordId(Long recordId);

    /**
     * 批量更新变更状态
     */
    int batchUpdateChangeStatus(@Param("recordId") Long recordId,
                                 @Param("changeStatus") String changeStatus,
                                 @Param("reviewComment") String reviewComment);
}
