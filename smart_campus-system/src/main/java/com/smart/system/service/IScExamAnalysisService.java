package com.smart.system.service;

import java.util.Map;
import com.smart.system.domain.ScExamRecord;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.domain.campusvo.ExamPaperDetailVo;
import com.smart.system.domain.dto.WrongQuestionRetryDto;

public interface IScExamAnalysisService {
    Map<String, Object> buildRecordOverview(ScExamRecord scExamRecord);

    Map<String, Object> buildRecordDetail(Long recordId);

    Map<String, Object> buildExamRuntime(Long recordId);

    Map<String, Object> buildWrongOverview(ScWrongQuestionBook scWrongQuestionBook);

    Map<String, Object> buildWrongDetail(Long wrongId);

    ExamPaperDetailVo buildWrongRetryPaper(WrongQuestionRetryDto dto, String operator);
}
