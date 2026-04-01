package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScAffairCategory;

public interface ScAffairCategoryMapper {
    ScAffairCategory selectScAffairCategoryByCategoryId(Long categoryId);

    ScAffairCategory selectScAffairCategoryByCategoryCode(String categoryCode);

    List<ScAffairCategory> selectScAffairCategoryList(ScAffairCategory scAffairCategory);

    int insertScAffairCategory(ScAffairCategory scAffairCategory);

    int updateScAffairCategory(ScAffairCategory scAffairCategory);

    int deleteScAffairCategoryByCategoryId(Long categoryId);

    int deleteScAffairCategoryByCategoryIds(Long[] categoryIds);
}
