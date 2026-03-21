package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScQuestionCatalog;

public interface IScQuestionCatalogService {
    ScQuestionCatalog selectScQuestionCatalogByCatalogId(Long catalogId);

    List<ScQuestionCatalog> selectScQuestionCatalogList(ScQuestionCatalog scQuestionCatalog);

    int insertScQuestionCatalog(ScQuestionCatalog scQuestionCatalog);

    int updateScQuestionCatalog(ScQuestionCatalog scQuestionCatalog);

    int deleteScQuestionCatalogByCatalogIds(Long[] catalogIds);
}

