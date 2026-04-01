package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScAffairTemplate;

public interface IScAffairTemplateService {
    ScAffairTemplate selectScAffairTemplateByTemplateId(Long templateId);

    List<ScAffairTemplate> selectScAffairTemplateList(ScAffairTemplate scAffairTemplate);

    List<ScAffairTemplate> selectEnabledTemplatesByRole(String portalRole);

    int insertScAffairTemplate(ScAffairTemplate scAffairTemplate);

    int updateScAffairTemplate(ScAffairTemplate scAffairTemplate);

    int deleteScAffairTemplateByTemplateIds(Long[] templateIds);
}
