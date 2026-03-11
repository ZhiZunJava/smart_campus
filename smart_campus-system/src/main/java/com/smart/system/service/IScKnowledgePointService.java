package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScKnowledgePoint;

public interface IScKnowledgePointService
{
    ScKnowledgePoint selectScKnowledgePointByKnowledgePointId(Long knowledgePointId);

    List<ScKnowledgePoint> selectScKnowledgePointList(ScKnowledgePoint scKnowledgePoint);

    int insertScKnowledgePoint(ScKnowledgePoint scKnowledgePoint);

    int updateScKnowledgePoint(ScKnowledgePoint scKnowledgePoint);

    int deleteScKnowledgePointByKnowledgePointIds(Long[] knowledgePointIds);

    int deleteScKnowledgePointByKnowledgePointId(Long knowledgePointId);
}
