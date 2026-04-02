package com.smart.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.smart.system.domain.ScQuestionItem;

public interface ScQuestionItemMapper {
    ScQuestionItem selectScQuestionItemByItemId(Long itemId);

    ScQuestionItem selectScQuestionItemBySource(@Param("sourceType") String sourceType, @Param("sourceRefId") Long sourceRefId);

    ScQuestionItem selectLatestScQuestionItemBySourceRefId(@Param("sourceRefId") Long sourceRefId);

    List<ScQuestionItem> selectScQuestionItemList(ScQuestionItem scQuestionItem);

    int insertScQuestionItem(ScQuestionItem scQuestionItem);

    int updateScQuestionItem(ScQuestionItem scQuestionItem);
}
