package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScResource;
import com.smart.system.mapper.ScResourceMapper;
import com.smart.system.service.IScResourceService;

@Service
public class ScResourceServiceImpl implements IScResourceService {
    @Autowired
    private ScResourceMapper scResourceMapper;

    @Override
    public ScResource selectScResourceByResourceId(Long resourceId) {
        return scResourceMapper.selectScResourceByResourceId(resourceId);
    }

    @Override
    public List<ScResource> selectScResourceList(ScResource scResource) {
        return scResourceMapper.selectScResourceList(scResource);
    }

    @Override
    public int insertScResource(ScResource scResource) {
        return scResourceMapper.insertScResource(scResource);
    }

    @Override
    public int updateScResource(ScResource scResource) {
        return scResourceMapper.updateScResource(scResource);
    }

    @Override
    public int deleteScResourceByResourceIds(Long[] resourceIds) {
        return scResourceMapper.deleteScResourceByResourceIds(resourceIds);
    }

    @Override
    public int deleteScResourceByResourceId(Long resourceId) {
        return scResourceMapper.deleteScResourceByResourceId(resourceId);
    }
}
