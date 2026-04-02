package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScQuestionItem;
import com.smart.system.domain.ScQuestionItemVersion;
import com.smart.system.domain.dto.QuestionItemUpsertDto;

public interface IScQuestionItemService {
    ScQuestionItem selectScQuestionItemByItemId(Long itemId);

    ScQuestionItem selectScQuestionItemBySource(String sourceType, Long sourceRefId);

    ScQuestionItem selectLatestScQuestionItemBySourceRefId(Long sourceRefId);

    List<ScQuestionItem> selectScQuestionItemList(ScQuestionItem scQuestionItem);

    List<ScQuestionItemVersion> selectScQuestionItemVersionByItemId(Long itemId);

    Long insertScQuestionItem(QuestionItemUpsertDto dto, String operator);

    Long updateScQuestionItem(QuestionItemUpsertDto dto, String operator);
}
