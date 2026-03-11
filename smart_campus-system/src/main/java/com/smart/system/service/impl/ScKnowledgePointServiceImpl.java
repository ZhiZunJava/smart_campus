package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScKnowledgePoint;
import com.smart.system.mapper.ScKnowledgePointMapper;
import com.smart.system.service.IScKnowledgePointService;

@Service
public class ScKnowledgePointServiceImpl implements IScKnowledgePointService
{
    @Autowired
    private ScKnowledgePointMapper scKnowledgePointMapper;

    @Override
    public ScKnowledgePoint selectScKnowledgePointByKnowledgePointId(Long knowledgePointId)
    {
        return scKnowledgePointMapper.selectScKnowledgePointByKnowledgePointId(knowledgePointId);
    }

    @Override
    public List<ScKnowledgePoint> selectScKnowledgePointList(ScKnowledgePoint scKnowledgePoint)
    {
        return scKnowledgePointMapper.selectScKnowledgePointList(scKnowledgePoint);
    }

    @Override
    public int insertScKnowledgePoint(ScKnowledgePoint scKnowledgePoint)
    {
        return scKnowledgePointMapper.insertScKnowledgePoint(scKnowledgePoint);
    }

    @Override
    public int updateScKnowledgePoint(ScKnowledgePoint scKnowledgePoint)
    {
        return scKnowledgePointMapper.updateScKnowledgePoint(scKnowledgePoint);
    }

    @Override
    public int deleteScKnowledgePointByKnowledgePointIds(Long[] knowledgePointIds)
    {
        return scKnowledgePointMapper.deleteScKnowledgePointByKnowledgePointIds(knowledgePointIds);
    }

    @Override
    public int deleteScKnowledgePointByKnowledgePointId(Long knowledgePointId)
    {
        return scKnowledgePointMapper.deleteScKnowledgePointByKnowledgePointId(knowledgePointId);
    }
}
