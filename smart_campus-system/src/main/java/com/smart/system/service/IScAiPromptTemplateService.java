package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScAiPromptTemplate;

public interface IScAiPromptTemplateService
{
    ScAiPromptTemplate selectScAiPromptTemplateByTemplateId(Long templateId);
    List<ScAiPromptTemplate> selectScAiPromptTemplateList(ScAiPromptTemplate scAiPromptTemplate);
    int insertScAiPromptTemplate(ScAiPromptTemplate scAiPromptTemplate);
    int updateScAiPromptTemplate(ScAiPromptTemplate scAiPromptTemplate);
    int deleteScAiPromptTemplateByTemplateIds(Long[] templateIds);
    int deleteScAiPromptTemplateByTemplateId(Long templateId);
}
