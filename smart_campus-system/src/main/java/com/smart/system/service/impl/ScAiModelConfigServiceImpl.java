package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScAiModelConfig;
import com.smart.system.mapper.ScAiModelConfigMapper;
import com.smart.system.service.IScAiModelConfigService;

@Service
public class ScAiModelConfigServiceImpl implements IScAiModelConfigService {
    @Autowired
    private ScAiModelConfigMapper scAiModelConfigMapper;

    @Override
    public ScAiModelConfig selectScAiModelConfigByModelId(Long modelId) {
        return scAiModelConfigMapper.selectScAiModelConfigByModelId(modelId);
    }

    @Override
    public List<ScAiModelConfig> selectScAiModelConfigList(ScAiModelConfig scAiModelConfig) {
        return scAiModelConfigMapper.selectScAiModelConfigList(scAiModelConfig);
    }

    @Override
    public int insertScAiModelConfig(ScAiModelConfig scAiModelConfig) {
        normalizeModelConfig(scAiModelConfig);
        if (isDefault(scAiModelConfig)) {
            clearOtherDefault(scAiModelConfig);
        }
        return scAiModelConfigMapper.insertScAiModelConfig(scAiModelConfig);
    }

    @Override
    public int updateScAiModelConfig(ScAiModelConfig scAiModelConfig) {
        normalizeModelConfig(scAiModelConfig);
        if (isDefault(scAiModelConfig)) {
            clearOtherDefault(scAiModelConfig);
        }
        return scAiModelConfigMapper.updateScAiModelConfig(scAiModelConfig);
    }

    @Override
    public int deleteScAiModelConfigByModelIds(Long[] modelIds) {
        return scAiModelConfigMapper.deleteScAiModelConfigByModelIds(modelIds);
    }

    @Override
    public int deleteScAiModelConfigByModelId(Long modelId) {
        return scAiModelConfigMapper.deleteScAiModelConfigByModelId(modelId);
    }

    @Override
    public ScAiModelConfig resolveModel(String modelType, String bizType, Long modelId) {
        String resolvedModelType = StringUtils.isEmpty(modelType) ? "chat" : modelType;
        if (modelId != null) {
            ScAiModelConfig directModel = scAiModelConfigMapper.selectScAiModelConfigByModelId(modelId);
            if (directModel == null) {
                throw new ServiceException("指定模型不存在");
            }
            if (!"0".equals(directModel.getStatus())) {
                throw new ServiceException("指定模型已停用");
            }
            if (StringUtils.isNotEmpty(resolvedModelType) && StringUtils.isNotEmpty(directModel.getModelType())
                    && !resolvedModelType.equalsIgnoreCase(directModel.getModelType())) {
                throw new ServiceException("指定模型类型与当前业务不匹配");
            }
            return directModel;
        }

        ScAiModelConfig query = new ScAiModelConfig();
        query.setStatus("0");
        query.setModelType(resolvedModelType);
        List<ScAiModelConfig> modelList = scAiModelConfigMapper.selectScAiModelConfigList(query);
        if (modelList == null || modelList.isEmpty()) {
            throw new ServiceException("未找到可用模型，请先在AI管理中配置模型");
        }

        ScAiModelConfig matchedModel = pickBusinessMatchedModel(modelList, bizType);
        if (matchedModel != null) {
            return matchedModel;
        }

        ScAiModelConfig defaultModel = modelList.stream().filter(this::isDefault).findFirst().orElse(null);
        if (defaultModel != null) {
            return defaultModel;
        }

        return modelList.get(0);
    }

    private ScAiModelConfig pickBusinessMatchedModel(List<ScAiModelConfig> modelList, String bizType) {
        if (StringUtils.isEmpty(bizType)) {
            return null;
        }
        String normalizedBizType = bizType.trim().toLowerCase();
        for (ScAiModelConfig modelConfig : modelList) {
            if (containsBizType(modelConfig.getBizTypes(), normalizedBizType)) {
                return modelConfig;
            }
        }
        for (ScAiModelConfig modelConfig : modelList) {
            if (containsBizType(modelConfig.getBizTypes(), "common")
                    || containsBizType(modelConfig.getBizTypes(), "all")) {
                return modelConfig;
            }
        }
        return null;
    }

    private boolean containsBizType(String bizTypes, String targetBizType) {
        if (StringUtils.isEmpty(bizTypes) || StringUtils.isEmpty(targetBizType)) {
            return false;
        }
        String[] values = bizTypes.split(",");
        for (String value : values) {
            if (targetBizType.equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }

    private void normalizeModelConfig(ScAiModelConfig scAiModelConfig) {
        if (scAiModelConfig == null) {
            return;
        }
        if (StringUtils.isEmpty(scAiModelConfig.getIsDefault())) {
            scAiModelConfig.setIsDefault("0");
        }
        if (scAiModelConfig.getPriority() == null) {
            scAiModelConfig.setPriority(0);
        }
        if (StringUtils.isNotEmpty(scAiModelConfig.getBizTypes())) {
            String normalized = scAiModelConfig.getBizTypes().replace("，", ",").replace(" ", "").toLowerCase();
            scAiModelConfig.setBizTypes(normalized);
        }
    }

    private boolean isDefault(ScAiModelConfig scAiModelConfig) {
        return scAiModelConfig != null && "1".equals(scAiModelConfig.getIsDefault());
    }

    private void clearOtherDefault(ScAiModelConfig scAiModelConfig) {
        ScAiModelConfig query = new ScAiModelConfig();
        query.setModelType(scAiModelConfig.getModelType());
        query.setStatus("0");
        List<ScAiModelConfig> modelList = scAiModelConfigMapper.selectScAiModelConfigList(query);
        for (ScAiModelConfig modelConfig : modelList) {
            if (modelConfig.getModelId() != null && !modelConfig.getModelId().equals(scAiModelConfig.getModelId())
                    && "1".equals(modelConfig.getIsDefault())) {
                modelConfig.setIsDefault("0");
                scAiModelConfigMapper.updateScAiModelConfig(modelConfig);
            }
        }
    }
}
