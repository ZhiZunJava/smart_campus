package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.campusvo.AiAsyncTaskSubmitVo;
import com.smart.system.domain.campusvo.AiQuestionGenerateResultVo;
import com.smart.system.domain.campusvo.ExamPaperDetailVo;
import com.smart.system.domain.campusvo.QuestionDetailVo;
import com.smart.system.domain.dto.AiGenerateQuestionDto;
import com.smart.system.domain.dto.ExamPaperAssembleDto;
import com.smart.system.domain.dto.PaperUpsertDto;
import com.smart.system.domain.dto.QuestionImportDto;
import com.smart.system.domain.dto.QuestionUpsertDto;

public interface IScExamManageService {
    QuestionDetailVo selectQuestionDetail(Long questionId);

    int saveQuestion(QuestionUpsertDto dto, String operator);

    int updateQuestion(QuestionUpsertDto dto, String operator);

    int deleteQuestionByIds(Long[] questionIds);

    Map<String, Object> importQuestions(List<QuestionImportDto> rows, boolean updateSupport, String operator);

    ExamPaperDetailVo selectPaperDetail(Long paperId);

    int savePaper(PaperUpsertDto dto, String operator);

    int updatePaper(PaperUpsertDto dto, String operator);

    int deletePaperByIds(Long[] paperIds);

    ExamPaperDetailVo assemblePaper(ExamPaperAssembleDto dto, String operator);

    AiQuestionGenerateResultVo generateQuestionsByAi(AiGenerateQuestionDto dto, String operator);

    AiAsyncTaskSubmitVo submitGenerateQuestionsByAiTask(AiGenerateQuestionDto dto, String operator);

    List<Map<String, Object>> listQuestionTypeMeta();
}
