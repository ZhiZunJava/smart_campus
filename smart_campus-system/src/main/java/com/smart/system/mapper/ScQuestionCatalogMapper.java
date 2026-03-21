package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQuestionCatalog;

public interface ScQuestionCatalogMapper {
    ScQuestionCatalog selectScQuestionCatalogByCatalogId(Long catalogId);

    List<ScQuestionCatalog> selectScQuestionCatalogList(ScQuestionCatalog scQuestionCatalog);

    int insertScQuestionCatalog(ScQuestionCatalog scQuestionCatalog);

    int updateScQuestionCatalog(ScQuestionCatalog scQuestionCatalog);

    int deleteScQuestionCatalogByCatalogId(Long catalogId);

    int deleteScQuestionCatalogByCatalogIds(Long[] catalogIds);
}

