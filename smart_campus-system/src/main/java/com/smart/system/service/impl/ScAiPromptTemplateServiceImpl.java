package com.smart.system.service.impl;

import java.util.List;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScAiPromptTemplate;
import com.smart.system.mapper.ScAiPromptTemplateMapper;
import com.smart.system.service.IScAiPromptTemplateService;

@Service
public class ScAiPromptTemplateServiceImpl implements IScAiPromptTemplateService {
    @Autowired
    private ScAiPromptTemplateMapper scAiPromptTemplateMapper;

    public ScAiPromptTemplate selectScAiPromptTemplateByTemplateId(Long templateId) {
        return scAiPromptTemplateMapper.selectScAiPromptTemplateByTemplateId(templateId);
    }

    @Override
    public ScAiPromptTemplate selectActiveTemplate(String bizType) {
        ScAiPromptTemplate query = new ScAiPromptTemplate();
        query.setStatus("0");
        if (StringUtils.isNotEmpty(bizType)) {
            query.setBizType(bizType);
            List<ScAiPromptTemplate> exactList = scAiPromptTemplateMapper.selectScAiPromptTemplateList(query);
            if (!CollectionUtils.isEmpty(exactList)) {
                return exactList.get(0);
            }
        }
        query.setBizType("common");
        List<ScAiPromptTemplate> commonList = scAiPromptTemplateMapper.selectScAiPromptTemplateList(query);
        return CollectionUtils.isEmpty(commonList) ? null : commonList.get(0);
    }

    public List<ScAiPromptTemplate> selectScAiPromptTemplateList(ScAiPromptTemplate scAiPromptTemplate) {
        return scAiPromptTemplateMapper.selectScAiPromptTemplateList(scAiPromptTemplate);
    }

    public int insertScAiPromptTemplate(ScAiPromptTemplate scAiPromptTemplate) {
        return scAiPromptTemplateMapper.insertScAiPromptTemplate(scAiPromptTemplate);
    }

    public int updateScAiPromptTemplate(ScAiPromptTemplate scAiPromptTemplate) {
        return scAiPromptTemplateMapper.updateScAiPromptTemplate(scAiPromptTemplate);
    }

    public int deleteScAiPromptTemplateByTemplateIds(Long[] templateIds) {
        return scAiPromptTemplateMapper.deleteScAiPromptTemplateByTemplateIds(templateIds);
    }

    public int deleteScAiPromptTemplateByTemplateId(Long templateId) {
        return scAiPromptTemplateMapper.deleteScAiPromptTemplateByTemplateId(templateId);
    }
}
