package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamStrategyTemplate;

public interface ScExamStrategyTemplateMapper {
    ScExamStrategyTemplate selectScExamStrategyTemplateByTemplateId(Long templateId);

    List<ScExamStrategyTemplate> selectScExamStrategyTemplateList(ScExamStrategyTemplate scExamStrategyTemplate);

    int insertScExamStrategyTemplate(ScExamStrategyTemplate scExamStrategyTemplate);

    int updateScExamStrategyTemplate(ScExamStrategyTemplate scExamStrategyTemplate);

    int deleteScExamStrategyTemplateByTemplateId(Long templateId);

    int deleteScExamStrategyTemplateByTemplateIds(Long[] templateIds);
}
