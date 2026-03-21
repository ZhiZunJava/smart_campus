package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScQuestionItemVersion;

public interface ScQuestionItemVersionMapper {
    ScQuestionItemVersion selectScQuestionItemVersionByVersionId(Long versionId);

    List<ScQuestionItemVersion> selectScQuestionItemVersionList(ScQuestionItemVersion scQuestionItemVersion);

    List<ScQuestionItemVersion> selectScQuestionItemVersionByItemId(Long itemId);

    int insertScQuestionItemVersion(ScQuestionItemVersion scQuestionItemVersion);

    int updateCurrentFlagByItemId(Long itemId);
}
