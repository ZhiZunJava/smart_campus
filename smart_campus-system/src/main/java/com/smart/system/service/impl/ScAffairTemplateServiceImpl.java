package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScAffairTemplate;
import com.smart.system.mapper.ScAffairTemplateMapper;
import com.smart.system.service.IScAffairTemplateService;

@Service
public class ScAffairTemplateServiceImpl implements IScAffairTemplateService {
    @Autowired
    private ScAffairTemplateMapper scAffairTemplateMapper;

    @Override
    public ScAffairTemplate selectScAffairTemplateByTemplateId(Long templateId) {
        return scAffairTemplateMapper.selectScAffairTemplateByTemplateId(templateId);
    }

    @Override
    public List<ScAffairTemplate> selectScAffairTemplateList(ScAffairTemplate scAffairTemplate) {
        return scAffairTemplateMapper.selectScAffairTemplateList(scAffairTemplate);
    }

    @Override
    public List<ScAffairTemplate> selectEnabledTemplatesByRole(String portalRole) {
        return scAffairTemplateMapper.selectEnabledTemplatesByRole(portalRole);
    }

    @Override
    public int insertScAffairTemplate(ScAffairTemplate scAffairTemplate) {
        return scAffairTemplateMapper.insertScAffairTemplate(scAffairTemplate);
    }

    @Override
    public int updateScAffairTemplate(ScAffairTemplate scAffairTemplate) {
        return scAffairTemplateMapper.updateScAffairTemplate(scAffairTemplate);
    }

    @Override
    public int deleteScAffairTemplateByTemplateIds(Long[] templateIds) {
        return scAffairTemplateMapper.deleteScAffairTemplateByTemplateIds(templateIds);
    }
}
