package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScAiModelConfig;

public interface ScAiModelConfigMapper {
    ScAiModelConfig selectScAiModelConfigByModelId(Long modelId);

    List<ScAiModelConfig> selectScAiModelConfigList(ScAiModelConfig scAiModelConfig);

    int insertScAiModelConfig(ScAiModelConfig scAiModelConfig);

    int updateScAiModelConfig(ScAiModelConfig scAiModelConfig);

    int deleteScAiModelConfigByModelId(Long modelId);

    int deleteScAiModelConfigByModelIds(Long[] modelIds);
}
