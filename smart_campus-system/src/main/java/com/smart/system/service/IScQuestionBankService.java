package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScQuestionBank;

public interface IScQuestionBankService
{
    ScQuestionBank selectScQuestionBankByQuestionId(Long questionId);
    List<ScQuestionBank> selectScQuestionBankList(ScQuestionBank scQuestionBank);
    int insertScQuestionBank(ScQuestionBank scQuestionBank);
    int updateScQuestionBank(ScQuestionBank scQuestionBank);
    int deleteScQuestionBankByQuestionIds(Long[] questionIds);
    int deleteScQuestionBankByQuestionId(Long questionId);
}
