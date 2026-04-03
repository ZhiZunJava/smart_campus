package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.common.exception.ServiceException;
import com.smart.common.utils.StringUtils;
import com.smart.system.domain.ScAiModelConfig;
import com.smart.system.mapper.ScAiModelConfigMapper;
import com.smart.system.service.IScAiModelConfigService;
import com.smart.system.service.ai.AiSecretCryptoService;

@Service
public class ScAiModelConfigServiceImpl implements IScAiModelConfigService {
    @Autowired
    private ScAiModelConfigMapper scAiModelConfigMapper;
    @Autowired
    private AiSecretCryptoService aiSecretCryptoService;

    @Override
    public ScAiModelConfig selectScAiModelConfigByModelId(Long modelId) {
        return decryptLoadedModel(scAiModelConfigMapper.selectScAiModelConfigByModelId(modelId));
    }

    @Override
    public List<ScAiModelConfig> selectScAiModelConfigList(ScAiModelConfig scAiModelConfig) {
        List<ScAiModelConfig> list = scAiModelConfigMapper.selectScAiModelConfigList(scAiModelConfig);
        if (list == null || list.isEmpty()) {
            return list;
        }
        for (ScAiModelConfig item : list) {
            decryptLoadedModel(item);
        }
        return list;
    }

    @Override
    public int insertScAiModelConfig(ScAiModelConfig scAiModelConfig) {
        normalizeModelConfig(scAiModelConfig);
        prepareApiKeyForPersistence(scAiModelConfig, null);
        if (isDefault(scAiModelConfig)) {
            clearOtherDefault(scAiModelConfig);
        }
        return scAiModelConfigMapper.insertScAiModelConfig(scAiModelConfig);
    }

    @Override
    public int updateScAiModelConfig(ScAiModelConfig scAiModelConfig) {
        normalizeModelConfig(scAiModelConfig);
        ScAiModelConfig existingModel = requireExistingModel(scAiModelConfig.getModelId());
        prepareApiKeyForPersistence(scAiModelConfig, existingModel);
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
            ScAiModelConfig directModel = selectScAiModelConfigByModelId(modelId);
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
        List<ScAiModelConfig> modelList = selectScAiModelConfigList(query);
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
        if (scAiModelConfig.getClearApiKey() == null) {
            scAiModelConfig.setClearApiKey(Boolean.FALSE);
        }
    }

    private boolean isDefault(ScAiModelConfig scAiModelConfig) {
        return scAiModelConfig != null && "1".equals(scAiModelConfig.getIsDefault());
    }

    private void clearOtherDefault(ScAiModelConfig scAiModelConfig) {
        ScAiModelConfig query = new ScAiModelConfig();
        query.setModelType(scAiModelConfig.getModelType());
        query.setStatus("0");
        List<ScAiModelConfig> modelList = selectScAiModelConfigList(query);
        for (ScAiModelConfig modelConfig : modelList) {
            if (modelConfig.getModelId() != null && !modelConfig.getModelId().equals(scAiModelConfig.getModelId())
                    && "1".equals(modelConfig.getIsDefault())) {
                modelConfig.setIsDefault("0");
                prepareApiKeyForPersistence(modelConfig, null);
                scAiModelConfigMapper.updateScAiModelConfig(modelConfig);
            }
        }
    }

    private ScAiModelConfig requireExistingModel(Long modelId) {
        if (modelId == null) {
            throw new ServiceException("模型ID不能为空");
        }
        ScAiModelConfig existingModel = selectScAiModelConfigByModelId(modelId);
        if (existingModel == null) {
            throw new ServiceException("模型配置不存在");
        }
        return existingModel;
    }

    private ScAiModelConfig decryptLoadedModel(ScAiModelConfig scAiModelConfig) {
        if (scAiModelConfig == null) {
            return null;
        }
        String decryptedApiKey = aiSecretCryptoService.decryptIfNeeded(scAiModelConfig.getApiKey());
        scAiModelConfig.setApiKey(decryptedApiKey);
        scAiModelConfig.setApiKeyConfigured(StringUtils.isNotEmpty(decryptedApiKey));
        scAiModelConfig.setClearApiKey(Boolean.FALSE);
        return scAiModelConfig;
    }

    private void prepareApiKeyForPersistence(ScAiModelConfig scAiModelConfig, ScAiModelConfig existingModel) {
        if (scAiModelConfig == null) {
            return;
        }
        if (Boolean.TRUE.equals(scAiModelConfig.getClearApiKey())) {
            scAiModelConfig.setApiKey("");
            scAiModelConfig.setApiKeyConfigured(Boolean.FALSE);
            return;
        }

        String apiKey = StringUtils.trim(scAiModelConfig.getApiKey());
        if (StringUtils.isEmpty(apiKey) && existingModel != null) {
            apiKey = StringUtils.trim(existingModel.getApiKey());
        }

        scAiModelConfig.setApiKey(aiSecretCryptoService.encryptIfNeeded(apiKey));
        scAiModelConfig.setApiKeyConfigured(StringUtils.isNotEmpty(apiKey));
    }
}
