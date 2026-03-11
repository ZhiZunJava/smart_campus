package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScAiPromptTemplate;
import com.smart.system.mapper.ScAiPromptTemplateMapper;
import com.smart.system.service.IScAiPromptTemplateService;

@Service
public class ScAiPromptTemplateServiceImpl implements IScAiPromptTemplateService
{
    @Autowired
    private ScAiPromptTemplateMapper scAiPromptTemplateMapper;
    public ScAiPromptTemplate selectScAiPromptTemplateByTemplateId(Long templateId) { return scAiPromptTemplateMapper.selectScAiPromptTemplateByTemplateId(templateId); }
    public List<ScAiPromptTemplate> selectScAiPromptTemplateList(ScAiPromptTemplate scAiPromptTemplate) { return scAiPromptTemplateMapper.selectScAiPromptTemplateList(scAiPromptTemplate); }
    public int insertScAiPromptTemplate(ScAiPromptTemplate scAiPromptTemplate) { return scAiPromptTemplateMapper.insertScAiPromptTemplate(scAiPromptTemplate); }
    public int updateScAiPromptTemplate(ScAiPromptTemplate scAiPromptTemplate) { return scAiPromptTemplateMapper.updateScAiPromptTemplate(scAiPromptTemplate); }
    public int deleteScAiPromptTemplateByTemplateIds(Long[] templateIds) { return scAiPromptTemplateMapper.deleteScAiPromptTemplateByTemplateIds(templateIds); }
    public int deleteScAiPromptTemplateByTemplateId(Long templateId) { return scAiPromptTemplateMapper.deleteScAiPromptTemplateByTemplateId(templateId); }
}
