package com.smart.system.service.impl;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScQuestionCatalog;
import com.smart.system.domain.ScQuestionCatalogScope;
import com.smart.system.mapper.ScQuestionCatalogMapper;
import com.smart.system.mapper.ScQuestionCatalogScopeMapper;
import com.smart.system.service.IScQuestionCatalogService;

@Service
public class ScQuestionCatalogServiceImpl implements IScQuestionCatalogService {
    @Autowired
    private ScQuestionCatalogMapper scQuestionCatalogMapper;

    @Autowired
    private ScQuestionCatalogScopeMapper scQuestionCatalogScopeMapper;

    @Override
    public ScQuestionCatalog selectScQuestionCatalogByCatalogId(Long catalogId) {
        ScQuestionCatalog catalog = scQuestionCatalogMapper.selectScQuestionCatalogByCatalogId(catalogId);
        if (catalog == null) {
            return null;
        }
        catalog.setScopes(scQuestionCatalogScopeMapper.selectScQuestionCatalogScopeByCatalogId(catalogId));
        return catalog;
    }

    @Override
    public List<ScQuestionCatalog> selectScQuestionCatalogList(ScQuestionCatalog scQuestionCatalog) {
        List<ScQuestionCatalog> catalogs = scQuestionCatalogMapper.selectScQuestionCatalogList(scQuestionCatalog);
        for (ScQuestionCatalog catalog : catalogs) {
            if (catalog != null && catalog.getCatalogId() != null) {
                catalog.setScopes(scQuestionCatalogScopeMapper.selectScQuestionCatalogScopeByCatalogId(catalog.getCatalogId()));
            }
        }
        return catalogs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertScQuestionCatalog(ScQuestionCatalog scQuestionCatalog) {
        normalizeCatalog(scQuestionCatalog);
        int rows = scQuestionCatalogMapper.insertScQuestionCatalog(scQuestionCatalog);
        replaceScopes(scQuestionCatalog);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateScQuestionCatalog(ScQuestionCatalog scQuestionCatalog) {
        normalizeCatalog(scQuestionCatalog);
        int rows = scQuestionCatalogMapper.updateScQuestionCatalog(scQuestionCatalog);
        replaceScopes(scQuestionCatalog);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteScQuestionCatalogByCatalogIds(Long[] catalogIds) {
        if (catalogIds == null || catalogIds.length == 0) {
            return 0;
        }
        for (Long catalogId : catalogIds) {
            scQuestionCatalogScopeMapper.deleteScQuestionCatalogScopeByCatalogId(catalogId);
        }
        return scQuestionCatalogMapper.deleteScQuestionCatalogByCatalogIds(catalogIds);
    }

    private void normalizeCatalog(ScQuestionCatalog scQuestionCatalog) {
        if (scQuestionCatalog == null) {
            return;
        }
        scQuestionCatalog.setVisibilityType(StringUtils.defaultIfEmpty(scQuestionCatalog.getVisibilityType(), "PRIVATE"));
        scQuestionCatalog.setStatus(StringUtils.defaultIfEmpty(scQuestionCatalog.getStatus(), "0"));
        if (scQuestionCatalog.getScopes() == null) {
            scQuestionCatalog.setScopes(Collections.emptyList());
        }
    }

    private void replaceScopes(ScQuestionCatalog scQuestionCatalog) {
        if (scQuestionCatalog == null || scQuestionCatalog.getCatalogId() == null) {
            return;
        }
        scQuestionCatalogScopeMapper.deleteScQuestionCatalogScopeByCatalogId(scQuestionCatalog.getCatalogId());
        for (ScQuestionCatalogScope scope : scQuestionCatalog.getScopes()) {
            if (scope == null || scope.getScopeRefId() == null || StringUtils.isEmpty(scope.getScopeType())) {
                continue;
            }
            scope.setCatalogId(scQuestionCatalog.getCatalogId());
            scope.setPermissionLevel(StringUtils.defaultIfEmpty(scope.getPermissionLevel(), "VIEW"));
            scope.setStatus(StringUtils.defaultIfEmpty(scope.getStatus(), "0"));
            scope.setCreateBy(StringUtils.defaultIfEmpty(scope.getCreateBy(),
                    StringUtils.defaultIfEmpty(scQuestionCatalog.getUpdateBy(), scQuestionCatalog.getCreateBy())));
            scope.setUpdateBy(scQuestionCatalog.getUpdateBy());
            scQuestionCatalogScopeMapper.insertScQuestionCatalogScope(scope);
        }
    }
}
