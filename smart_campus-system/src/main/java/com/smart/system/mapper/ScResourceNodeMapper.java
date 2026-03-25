package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScResourceNode;

public interface ScResourceNodeMapper {
    ScResourceNode selectScResourceNodeByNodeId(Long nodeId);

    List<ScResourceNode> selectScResourceNodeList(ScResourceNode scResourceNode);

    int insertScResourceNode(ScResourceNode scResourceNode);

    int updateScResourceNode(ScResourceNode scResourceNode);

    int deleteScResourceNodeByNodeId(Long nodeId);

    int deleteScResourceNodeByNodeIds(Long[] nodeIds);
}
