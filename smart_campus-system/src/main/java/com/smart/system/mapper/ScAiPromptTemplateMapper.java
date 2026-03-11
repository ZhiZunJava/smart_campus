package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScAiPromptTemplate;

public interface ScAiPromptTemplateMapper
{
    ScAiPromptTemplate selectScAiPromptTemplateByTemplateId(Long templateId);
    List<ScAiPromptTemplate> selectScAiPromptTemplateList(ScAiPromptTemplate scAiPromptTemplate);
    int insertScAiPromptTemplate(ScAiPromptTemplate scAiPromptTemplate);
    int updateScAiPromptTemplate(ScAiPromptTemplate scAiPromptTemplate);
    int deleteScAiPromptTemplateByTemplateId(Long templateId);
    int deleteScAiPromptTemplateByTemplateIds(Long[] templateIds);
}
