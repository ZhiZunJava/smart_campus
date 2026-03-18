package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScResource;

public interface ScResourceMapper {
    ScResource selectScResourceByResourceId(Long resourceId);

    List<ScResource> selectScResourceList(ScResource scResource);

    int insertScResource(ScResource scResource);

    int updateScResource(ScResource scResource);

    int deleteScResourceByResourceId(Long resourceId);

    int deleteScResourceByResourceIds(Long[] resourceIds);

    int increaseViewCount(Long resourceId);

    int increaseDownloadCount(Long resourceId);

    int increaseFavoriteCount(Long resourceId);
}
