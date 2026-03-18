package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQuestionBank;

public interface ScQuestionBankMapper {
    ScQuestionBank selectScQuestionBankByQuestionId(Long questionId);

    List<ScQuestionBank> selectScQuestionBankList(ScQuestionBank scQuestionBank);

    int insertScQuestionBank(ScQuestionBank scQuestionBank);

    int updateScQuestionBank(ScQuestionBank scQuestionBank);

    int deleteScQuestionBankByQuestionId(Long questionId);

    int deleteScQuestionBankByQuestionIds(Long[] questionIds);
}
