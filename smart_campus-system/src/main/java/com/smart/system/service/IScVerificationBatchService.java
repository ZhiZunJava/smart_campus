package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScVerificationBatch;
import com.smart.system.domain.ScVerificationRecord;
import com.smart.system.domain.ScVerificationChange;
import com.smart.system.domain.dto.VerificationBatchCreateDto;
import com.smart.system.domain.dto.VerificationSubmitDto;
import com.smart.system.domain.dto.VerificationReviewDto;
import com.smart.system.domain.dto.VerificationBatchReviewDto;
import com.smart.system.domain.dto.VerificationBatchModifyDto;

/**
 * 学籍核对批次 Service 接口
 *
 * @author can
 */
public interface IScVerificationBatchService {

    /** 创建批次 */
    int createBatch(VerificationBatchCreateDto dto, String operator);

    /** 更新批次 */
    int updateBatch(VerificationBatchCreateDto dto, String operator);

    /** 激活批次（DRAFT -> ACTIVE），同时初始化学生记录 */
    int activateBatch(Long batchId, String operator);

    /** 关闭批次（ACTIVE -> CLOSED） */
    int closeBatch(Long batchId, String operator);

    /** 删除批次（级联删除记录和变更） */
    int deleteBatchByIds(Long[] batchIds);

    /** 分页查询批次列表 */
    List<ScVerificationBatch> selectBatchList(ScVerificationBatch query);

    /** 批次详情（含统计数据） */
    ScVerificationBatch selectBatchDetail(Long batchId);

    /** 查询某批次下的学生记录列表 */
    List<ScVerificationRecord> selectRecordList(ScVerificationRecord query);

    /** 查询单条记录详情（含变更明细） */
    ScVerificationRecord selectRecordDetail(Long recordId);

    /* ---- 学生端 ---- */

    /** 获取学生可参与的活跃批次 */
    List<ScVerificationBatch> selectActiveBatchesForStudent(Long userId);

    /** 学生获取自己在某批次的学籍信息+可编辑字段+已提交变更 */
    ScVerificationRecord getStudentVerificationInfo(Long batchId, Long userId);

    /** 学生确认无需修改 */
    int confirmNoChange(Long recordId, Long userId);

    /** 学生提交变更 */
    int submitChanges(VerificationSubmitDto dto, Long userId);

    /** 查看学生自己的所有核对记录 */
    List<ScVerificationRecord> selectStudentRecords(Long userId);

    /* ---- 审核 ---- */

    /** 审核单条记录 */
    int reviewRecord(VerificationReviewDto dto, String operator);

    /** 批量审核 */
    int batchReviewRecords(VerificationBatchReviewDto dto, String operator);

    /** 管理员批量直接修改 */
    int adminBatchModify(VerificationBatchModifyDto dto, String operator);
}
