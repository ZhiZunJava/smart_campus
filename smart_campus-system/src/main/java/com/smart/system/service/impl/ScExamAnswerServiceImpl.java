package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScExamAnswer;
import com.smart.system.mapper.ScExamAnswerMapper;
import com.smart.system.service.IScExamAnswerService;

@Service
public class ScExamAnswerServiceImpl implements IScExamAnswerService {
    @Autowired
    private ScExamAnswerMapper scExamAnswerMapper;

    public ScExamAnswer selectScExamAnswerByAnswerId(Long answerId) {
        return scExamAnswerMapper.selectScExamAnswerByAnswerId(answerId);
    }

    public List<ScExamAnswer> selectScExamAnswerList(ScExamAnswer scExamAnswer) {
        return scExamAnswerMapper.selectScExamAnswerList(scExamAnswer);
    }

    public List<ScExamAnswer> selectScExamAnswerByRecordId(Long recordId) {
        return scExamAnswerMapper.selectScExamAnswerByRecordId(recordId);
    }

    public int insertScExamAnswer(ScExamAnswer scExamAnswer) {
        return scExamAnswerMapper.insertScExamAnswer(scExamAnswer);
    }

    public int updateScExamAnswer(ScExamAnswer scExamAnswer) {
        return scExamAnswerMapper.updateScExamAnswer(scExamAnswer);
    }

    public int deleteScExamAnswerByAnswerIds(Long[] answerIds) {
        return scExamAnswerMapper.deleteScExamAnswerByAnswerIds(answerIds);
    }

    public int deleteScExamAnswerByAnswerId(Long answerId) {
        return scExamAnswerMapper.deleteScExamAnswerByAnswerId(answerId);
    }

    public int deleteScExamAnswerByRecordId(Long recordId) {
        return scExamAnswerMapper.deleteScExamAnswerByRecordId(recordId);
    }
}
