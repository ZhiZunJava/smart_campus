package com.smart.system.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface ScAffairSpecialRecordMapper {
    int upsertAskLeaveRecord(Map<String, Object> params);

    int upsertLeaveReturnRecord(Map<String, Object> params);

    int upsertActivityRecord(Map<String, Object> params);

    int upsertCompetitionRecord(Map<String, Object> params);

    int upsertInnovationRecord(Map<String, Object> params);

    int upsertEvaluationRecord(Map<String, Object> params);

    int upsertTextbookRecord(Map<String, Object> params);

    int upsertAcademicStatusRecord(Map<String, Object> params);

    Map<String, Object> selectAskLeaveRecordByRequestId(@Param("requestId") Long requestId);

    Map<String, Object> selectLeaveReturnRecordByRequestId(@Param("requestId") Long requestId);

    Map<String, Object> selectActivityRecordByRequestId(@Param("requestId") Long requestId);

    Map<String, Object> selectCompetitionRecordByRequestId(@Param("requestId") Long requestId);

    Map<String, Object> selectInnovationRecordByRequestId(@Param("requestId") Long requestId);

    Map<String, Object> selectEvaluationRecordByRequestId(@Param("requestId") Long requestId);

    Map<String, Object> selectTextbookRecordByRequestId(@Param("requestId") Long requestId);

    Map<String, Object> selectAcademicStatusRecordByRequestId(@Param("requestId") Long requestId);

    List<Map<String, Object>> selectTextbookRecordsByApplicantUserId(@Param("applicantUserId") Long applicantUserId);

    List<Map<String, Object>> selectAcademicStatusRecordsByApplicantUserId(@Param("applicantUserId") Long applicantUserId);

    List<Map<String, Object>> selectAskLeaveRecordsByApplicantUserId(@Param("applicantUserId") Long applicantUserId);

    List<Map<String, Object>> selectLeaveReturnRecordsByApplicantUserId(@Param("applicantUserId") Long applicantUserId);

    List<Map<String, Object>> selectTextbookClaimRecordsByIsbn(@Param("isbn") String isbn);
}
