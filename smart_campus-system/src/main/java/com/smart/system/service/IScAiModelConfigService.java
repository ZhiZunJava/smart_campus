package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScAiModelConfig;

public interface IScAiModelConfigService
{
    ScAiModelConfig selectScAiModelConfigByModelId(Long modelId);
    List<ScAiModelConfig> selectScAiModelConfigList(ScAiModelConfig scAiModelConfig);
    int insertScAiModelConfig(ScAiModelConfig scAiModelConfig);
    int updateScAiModelConfig(ScAiModelConfig scAiModelConfig);
    int deleteScAiModelConfigByModelIds(Long[] modelIds);
    int deleteScAiModelConfigByModelId(Long modelId);
    ScAiModelConfig resolveModel(String modelType, String bizType, Long modelId);
}
