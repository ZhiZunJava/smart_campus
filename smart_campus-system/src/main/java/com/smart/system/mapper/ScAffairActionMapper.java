package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScAffairAction;

public interface ScAffairActionMapper {
    List<ScAffairAction> selectScAffairActionListByRequestId(Long requestId);

    int insertScAffairAction(ScAffairAction scAffairAction);
}
