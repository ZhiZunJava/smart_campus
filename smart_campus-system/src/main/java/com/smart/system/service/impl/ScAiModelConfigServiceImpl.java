package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScAiModelConfig;
import com.smart.system.mapper.ScAiModelConfigMapper;
import com.smart.system.service.IScAiModelConfigService;

@Service
public class ScAiModelConfigServiceImpl implements IScAiModelConfigService
{
    @Autowired
    private ScAiModelConfigMapper scAiModelConfigMapper;
    public ScAiModelConfig selectScAiModelConfigByModelId(Long modelId) { return scAiModelConfigMapper.selectScAiModelConfigByModelId(modelId); }
    public List<ScAiModelConfig> selectScAiModelConfigList(ScAiModelConfig scAiModelConfig) { return scAiModelConfigMapper.selectScAiModelConfigList(scAiModelConfig); }
    public int insertScAiModelConfig(ScAiModelConfig scAiModelConfig) { return scAiModelConfigMapper.insertScAiModelConfig(scAiModelConfig); }
    public int updateScAiModelConfig(ScAiModelConfig scAiModelConfig) { return scAiModelConfigMapper.updateScAiModelConfig(scAiModelConfig); }
    public int deleteScAiModelConfigByModelIds(Long[] modelIds) { return scAiModelConfigMapper.deleteScAiModelConfigByModelIds(modelIds); }
    public int deleteScAiModelConfigByModelId(Long modelId) { return scAiModelConfigMapper.deleteScAiModelConfigByModelId(modelId); }
}
