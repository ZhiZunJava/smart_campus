package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScAffairTemplate;

public interface ScAffairTemplateMapper {
    ScAffairTemplate selectScAffairTemplateByTemplateId(Long templateId);

    List<ScAffairTemplate> selectScAffairTemplateList(ScAffairTemplate scAffairTemplate);

    List<ScAffairTemplate> selectEnabledTemplatesByRole(String portalRole);

    int insertScAffairTemplate(ScAffairTemplate scAffairTemplate);

    int updateScAffairTemplate(ScAffairTemplate scAffairTemplate);

    int deleteScAffairTemplateByTemplateId(Long templateId);

    int deleteScAffairTemplateByTemplateIds(Long[] templateIds);
}
