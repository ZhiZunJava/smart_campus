package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScWrongQuestionBook;
import com.smart.system.mapper.ScWrongQuestionBookMapper;
import com.smart.system.service.IScWrongQuestionBookService;

@Service
public class ScWrongQuestionBookServiceImpl implements IScWrongQuestionBookService {
    @Autowired
    private ScWrongQuestionBookMapper scWrongQuestionBookMapper;

    public ScWrongQuestionBook selectScWrongQuestionBookById(Long id) {
        return scWrongQuestionBookMapper.selectScWrongQuestionBookById(id);
    }

    public ScWrongQuestionBook selectScWrongQuestionBookByUserAndQuestion(Long userId, Long questionId) {
        return scWrongQuestionBookMapper.selectScWrongQuestionBookByUserAndQuestion(userId, questionId);
    }

    public List<ScWrongQuestionBook> selectScWrongQuestionBookList(ScWrongQuestionBook scWrongQuestionBook) {
        return scWrongQuestionBookMapper.selectScWrongQuestionBookList(scWrongQuestionBook);
    }

    public int insertScWrongQuestionBook(ScWrongQuestionBook scWrongQuestionBook) {
        return scWrongQuestionBookMapper.insertScWrongQuestionBook(scWrongQuestionBook);
    }

    public int updateScWrongQuestionBook(ScWrongQuestionBook scWrongQuestionBook) {
        return scWrongQuestionBookMapper.updateScWrongQuestionBook(scWrongQuestionBook);
    }

    public int deleteScWrongQuestionBookByIds(Long[] ids) {
        return scWrongQuestionBookMapper.deleteScWrongQuestionBookByIds(ids);
    }

    public int deleteScWrongQuestionBookById(Long id) {
        return scWrongQuestionBookMapper.deleteScWrongQuestionBookById(id);
    }
}
