package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScResourceAsset;

public interface ScResourceAssetMapper {
    ScResourceAsset selectScResourceAssetByAssetId(Long assetId);

    List<ScResourceAsset> selectScResourceAssetList(ScResourceAsset scResourceAsset);

    int insertScResourceAsset(ScResourceAsset scResourceAsset);

    int updateScResourceAsset(ScResourceAsset scResourceAsset);

    int deleteScResourceAssetByAssetId(Long assetId);

    int deleteScResourceAssetByAssetIds(Long[] assetIds);

    int deleteScResourceAssetByResourceId(Long resourceId);
}
