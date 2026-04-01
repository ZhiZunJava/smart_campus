package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScAffairCategory;
import com.smart.system.mapper.ScAffairCategoryMapper;
import com.smart.system.service.IScAffairCategoryService;

@Service
public class ScAffairCategoryServiceImpl implements IScAffairCategoryService {
    @Autowired
    private ScAffairCategoryMapper scAffairCategoryMapper;

    @Override
    public ScAffairCategory selectScAffairCategoryByCategoryId(Long categoryId) {
        return scAffairCategoryMapper.selectScAffairCategoryByCategoryId(categoryId);
    }

    @Override
    public ScAffairCategory selectScAffairCategoryByCategoryCode(String categoryCode) {
        return scAffairCategoryMapper.selectScAffairCategoryByCategoryCode(categoryCode);
    }

    @Override
    public List<ScAffairCategory> selectScAffairCategoryList(ScAffairCategory scAffairCategory) {
        return scAffairCategoryMapper.selectScAffairCategoryList(scAffairCategory);
    }

    @Override
    public int insertScAffairCategory(ScAffairCategory scAffairCategory) {
        return scAffairCategoryMapper.insertScAffairCategory(scAffairCategory);
    }

    @Override
    public int updateScAffairCategory(ScAffairCategory scAffairCategory) {
        return scAffairCategoryMapper.updateScAffairCategory(scAffairCategory);
    }

    @Override
    public int deleteScAffairCategoryByCategoryIds(Long[] categoryIds) {
        return scAffairCategoryMapper.deleteScAffairCategoryByCategoryIds(categoryIds);
    }
}
