package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScWrongQuestionBook;

public interface ScWrongQuestionBookMapper
{
    ScWrongQuestionBook selectScWrongQuestionBookById(Long id);
    ScWrongQuestionBook selectScWrongQuestionBookByUserAndQuestion(Long userId, Long questionId);
    List<ScWrongQuestionBook> selectScWrongQuestionBookList(ScWrongQuestionBook scWrongQuestionBook);
    int insertScWrongQuestionBook(ScWrongQuestionBook scWrongQuestionBook);
    int updateScWrongQuestionBook(ScWrongQuestionBook scWrongQuestionBook);
    int deleteScWrongQuestionBookById(Long id);
    int deleteScWrongQuestionBookByIds(Long[] ids);
}
