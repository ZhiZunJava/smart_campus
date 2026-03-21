package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScExamStrategyTemplate;
import com.smart.system.mapper.ScExamStrategyTemplateMapper;
import com.smart.system.service.IScExamStrategyTemplateService;

@Service
public class ScExamStrategyTemplateServiceImpl implements IScExamStrategyTemplateService {
    @Autowired
    private ScExamStrategyTemplateMapper scExamStrategyTemplateMapper;

    public ScExamStrategyTemplate selectScExamStrategyTemplateByTemplateId(Long templateId) {
        return scExamStrategyTemplateMapper.selectScExamStrategyTemplateByTemplateId(templateId);
    }

    public List<ScExamStrategyTemplate> selectScExamStrategyTemplateList(
            ScExamStrategyTemplate scExamStrategyTemplate) {
        return scExamStrategyTemplateMapper.selectScExamStrategyTemplateList(scExamStrategyTemplate);
    }

    public int insertScExamStrategyTemplate(ScExamStrategyTemplate scExamStrategyTemplate) {
        return scExamStrategyTemplateMapper.insertScExamStrategyTemplate(scExamStrategyTemplate);
    }

    public int updateScExamStrategyTemplate(ScExamStrategyTemplate scExamStrategyTemplate) {
        return scExamStrategyTemplateMapper.updateScExamStrategyTemplate(scExamStrategyTemplate);
    }

    public int deleteScExamStrategyTemplateByTemplateIds(Long[] templateIds) {
        return scExamStrategyTemplateMapper.deleteScExamStrategyTemplateByTemplateIds(templateIds);
    }

    public int deleteScExamStrategyTemplateByTemplateId(Long templateId) {
        return scExamStrategyTemplateMapper.deleteScExamStrategyTemplateByTemplateId(templateId);
    }
}
