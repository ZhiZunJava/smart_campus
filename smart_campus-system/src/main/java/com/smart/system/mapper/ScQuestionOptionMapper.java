package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQuestionOption;

public interface ScQuestionOptionMapper {
    ScQuestionOption selectScQuestionOptionByOptionId(Long optionId);

    List<ScQuestionOption> selectScQuestionOptionList(ScQuestionOption scQuestionOption);

    List<ScQuestionOption> selectScQuestionOptionByQuestionId(Long questionId);

    int insertScQuestionOption(ScQuestionOption scQuestionOption);

    int updateScQuestionOption(ScQuestionOption scQuestionOption);

    int deleteScQuestionOptionByOptionId(Long optionId);

    int deleteScQuestionOptionByOptionIds(Long[] optionIds);

    int deleteScQuestionOptionByQuestionId(Long questionId);
}
