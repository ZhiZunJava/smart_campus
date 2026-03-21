package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamAnswerDraft;

public interface ScExamAnswerDraftMapper {
    ScExamAnswerDraft selectScExamAnswerDraftByRecordAndQuestion(Long recordId, Long questionId);

    List<ScExamAnswerDraft> selectScExamAnswerDraftList(ScExamAnswerDraft scExamAnswerDraft);

    int insertScExamAnswerDraft(ScExamAnswerDraft scExamAnswerDraft);

    int updateScExamAnswerDraft(ScExamAnswerDraft scExamAnswerDraft);

    int deleteScExamAnswerDraftByRecordId(Long recordId);
}

