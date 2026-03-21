package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQuestionCatalogScope;

public interface ScQuestionCatalogScopeMapper {
    List<ScQuestionCatalogScope> selectScQuestionCatalogScopeByCatalogId(Long catalogId);

    int insertScQuestionCatalogScope(ScQuestionCatalogScope scQuestionCatalogScope);

    int deleteScQuestionCatalogScopeByCatalogId(Long catalogId);
}

