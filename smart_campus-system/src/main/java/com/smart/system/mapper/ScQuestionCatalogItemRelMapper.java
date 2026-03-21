package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQuestionCatalogItemRel;

public interface ScQuestionCatalogItemRelMapper {
    List<ScQuestionCatalogItemRel> selectScQuestionCatalogItemRelList(ScQuestionCatalogItemRel scQuestionCatalogItemRel);

    int insertScQuestionCatalogItemRel(ScQuestionCatalogItemRel scQuestionCatalogItemRel);

    int deleteScQuestionCatalogItemRelByItemId(Long itemId);
}

