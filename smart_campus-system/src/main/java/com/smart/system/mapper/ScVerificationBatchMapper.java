package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScVerificationBatch;
import org.apache.ibatis.annotations.Param;

/**
 * 学籍核对批次 Mapper
 *
 * @author can
 */
public interface ScVerificationBatchMapper {
    ScVerificationBatch selectScVerificationBatchByBatchId(Long batchId);

    List<ScVerificationBatch> selectScVerificationBatchList(ScVerificationBatch query);

    /**
     * 查询学生可参与的活跃批次
     */
    List<ScVerificationBatch> selectActiveBatchesForStudent(@Param("userId") Long userId,
                                                            @Param("gradeId") Long gradeId,
                                                            @Param("classId") Long classId);

    int insertScVerificationBatch(ScVerificationBatch batch);

    int updateScVerificationBatch(ScVerificationBatch batch);

    int deleteScVerificationBatchByBatchId(Long batchId);

    int deleteScVerificationBatchByBatchIds(Long[] batchIds);

    /**
     * 获取当天最大批次编号序号
     */
    Integer selectMaxBatchNoSeq(@Param("prefix") String prefix);
}
