package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScQuestionOption;

public interface IScQuestionOptionService {
    ScQuestionOption selectScQuestionOptionByOptionId(Long optionId);

    List<ScQuestionOption> selectScQuestionOptionList(ScQuestionOption scQuestionOption);

    List<ScQuestionOption> selectScQuestionOptionByQuestionId(Long questionId);

    int insertScQuestionOption(ScQuestionOption scQuestionOption);

    int updateScQuestionOption(ScQuestionOption scQuestionOption);

    int deleteScQuestionOptionByOptionIds(Long[] optionIds);

    int deleteScQuestionOptionByOptionId(Long optionId);

    int deleteScQuestionOptionByQuestionId(Long questionId);
}
