package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScResource;

public interface IScResourceService
{
    ScResource selectScResourceByResourceId(Long resourceId);
    List<ScResource> selectScResourceList(ScResource scResource);
    int insertScResource(ScResource scResource);
    int updateScResource(ScResource scResource);
    int deleteScResourceByResourceIds(Long[] resourceIds);
    int deleteScResourceByResourceId(Long resourceId);
}
