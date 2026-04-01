package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScAffairCategory;

public interface IScAffairCategoryService {
    ScAffairCategory selectScAffairCategoryByCategoryId(Long categoryId);

    ScAffairCategory selectScAffairCategoryByCategoryCode(String categoryCode);

    List<ScAffairCategory> selectScAffairCategoryList(ScAffairCategory scAffairCategory);

    int insertScAffairCategory(ScAffairCategory scAffairCategory);

    int updateScAffairCategory(ScAffairCategory scAffairCategory);

    int deleteScAffairCategoryByCategoryIds(Long[] categoryIds);
}
