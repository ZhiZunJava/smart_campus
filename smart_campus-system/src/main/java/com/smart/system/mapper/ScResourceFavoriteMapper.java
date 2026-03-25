package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScResource;
import com.smart.system.domain.ScResourceFavorite;

public interface ScResourceFavoriteMapper {
    ScResourceFavorite selectByResourceAndUser(Long resourceId, Long userId);

    int insertScResourceFavorite(ScResourceFavorite scResourceFavorite);

    int deleteByResourceAndUser(Long resourceId, Long userId);

    int countByResourceId(Long resourceId);

    List<ScResource> selectFavoriteResourceList(Long userId);
}
