package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScQuestionBank;
import com.smart.system.mapper.ScQuestionBankMapper;
import com.smart.system.service.IScQuestionBankService;

@Service
public class ScQuestionBankServiceImpl implements IScQuestionBankService
{
    @Autowired
    private ScQuestionBankMapper scQuestionBankMapper;
    public ScQuestionBank selectScQuestionBankByQuestionId(Long questionId) { return scQuestionBankMapper.selectScQuestionBankByQuestionId(questionId); }
    public List<ScQuestionBank> selectScQuestionBankList(ScQuestionBank scQuestionBank) { return scQuestionBankMapper.selectScQuestionBankList(scQuestionBank); }
    public int insertScQuestionBank(ScQuestionBank scQuestionBank) { return scQuestionBankMapper.insertScQuestionBank(scQuestionBank); }
    public int updateScQuestionBank(ScQuestionBank scQuestionBank) { return scQuestionBankMapper.updateScQuestionBank(scQuestionBank); }
    public int deleteScQuestionBankByQuestionIds(Long[] questionIds) { return scQuestionBankMapper.deleteScQuestionBankByQuestionIds(questionIds); }
    public int deleteScQuestionBankByQuestionId(Long questionId) { return scQuestionBankMapper.deleteScQuestionBankByQuestionId(questionId); }
}
