package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScExamStrategyTemplate;

public interface IScExamStrategyTemplateService {
    ScExamStrategyTemplate selectScExamStrategyTemplateByTemplateId(Long templateId);

    List<ScExamStrategyTemplate> selectScExamStrategyTemplateList(ScExamStrategyTemplate scExamStrategyTemplate);

    int insertScExamStrategyTemplate(ScExamStrategyTemplate scExamStrategyTemplate);

    int updateScExamStrategyTemplate(ScExamStrategyTemplate scExamStrategyTemplate);

    int deleteScExamStrategyTemplateByTemplateIds(Long[] templateIds);

    int deleteScExamStrategyTemplateByTemplateId(Long templateId);
}
