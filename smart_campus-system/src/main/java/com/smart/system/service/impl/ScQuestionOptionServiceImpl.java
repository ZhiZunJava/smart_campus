package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScQuestionOption;
import com.smart.system.mapper.ScQuestionOptionMapper;
import com.smart.system.service.IScQuestionOptionService;

@Service
public class ScQuestionOptionServiceImpl implements IScQuestionOptionService
{
    @Autowired
    private ScQuestionOptionMapper scQuestionOptionMapper;
    public ScQuestionOption selectScQuestionOptionByOptionId(Long optionId) { return scQuestionOptionMapper.selectScQuestionOptionByOptionId(optionId); }
    public List<ScQuestionOption> selectScQuestionOptionList(ScQuestionOption scQuestionOption) { return scQuestionOptionMapper.selectScQuestionOptionList(scQuestionOption); }
    public List<ScQuestionOption> selectScQuestionOptionByQuestionId(Long questionId) { return scQuestionOptionMapper.selectScQuestionOptionByQuestionId(questionId); }
    public int insertScQuestionOption(ScQuestionOption scQuestionOption) { return scQuestionOptionMapper.insertScQuestionOption(scQuestionOption); }
    public int updateScQuestionOption(ScQuestionOption scQuestionOption) { return scQuestionOptionMapper.updateScQuestionOption(scQuestionOption); }
    public int deleteScQuestionOptionByOptionIds(Long[] optionIds) { return scQuestionOptionMapper.deleteScQuestionOptionByOptionIds(optionIds); }
    public int deleteScQuestionOptionByOptionId(Long optionId) { return scQuestionOptionMapper.deleteScQuestionOptionByOptionId(optionId); }
    public int deleteScQuestionOptionByQuestionId(Long questionId) { return scQuestionOptionMapper.deleteScQuestionOptionByQuestionId(questionId); }
}
