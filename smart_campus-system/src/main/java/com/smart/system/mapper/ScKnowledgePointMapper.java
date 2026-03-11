package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScKnowledgePoint;

public interface ScKnowledgePointMapper
{
    ScKnowledgePoint selectScKnowledgePointByKnowledgePointId(Long knowledgePointId);

    List<ScKnowledgePoint> selectScKnowledgePointList(ScKnowledgePoint scKnowledgePoint);

    int insertScKnowledgePoint(ScKnowledgePoint scKnowledgePoint);

    int updateScKnowledgePoint(ScKnowledgePoint scKnowledgePoint);

    int deleteScKnowledgePointByKnowledgePointId(Long knowledgePointId);

    int deleteScKnowledgePointByKnowledgePointIds(Long[] knowledgePointIds);
}
