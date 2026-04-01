package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScTextbook;

public interface ScTextbookMapper {
    ScTextbook selectScTextbookByTextbookId(Long textbookId);

    List<ScTextbook> selectScTextbookList(ScTextbook query);

    int insertScTextbook(ScTextbook scTextbook);

    int updateScTextbook(ScTextbook scTextbook);

    int deleteScTextbookByTextbookId(Long textbookId);

    int deleteScTextbookByTextbookIds(Long[] textbookIds);
}
