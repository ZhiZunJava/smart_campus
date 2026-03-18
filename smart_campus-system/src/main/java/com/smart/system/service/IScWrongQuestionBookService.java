package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScWrongQuestionBook;

public interface IScWrongQuestionBookService {
    ScWrongQuestionBook selectScWrongQuestionBookById(Long id);

    ScWrongQuestionBook selectScWrongQuestionBookByUserAndQuestion(Long userId, Long questionId);

    List<ScWrongQuestionBook> selectScWrongQuestionBookList(ScWrongQuestionBook scWrongQuestionBook);

    int insertScWrongQuestionBook(ScWrongQuestionBook scWrongQuestionBook);

    int updateScWrongQuestionBook(ScWrongQuestionBook scWrongQuestionBook);

    int deleteScWrongQuestionBookByIds(Long[] ids);

    int deleteScWrongQuestionBookById(Long id);
}
