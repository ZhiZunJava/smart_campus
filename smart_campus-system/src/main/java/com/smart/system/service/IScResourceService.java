package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.campusvo.ResourceOperationOverviewVo;

public interface IScResourceService {
    ScResource selectScResourceByResourceId(Long resourceId);

    List<ScResource> selectScResourceList(ScResource scResource);

    int insertScResource(ScResource scResource);

    int updateScResource(ScResource scResource);

    int deleteScResourceByResourceIds(Long[] resourceIds);

    int deleteScResourceByResourceId(Long resourceId);

    ResourceOperationOverviewVo getResourceOperationOverview(Long courseId, Integer limit);
}
