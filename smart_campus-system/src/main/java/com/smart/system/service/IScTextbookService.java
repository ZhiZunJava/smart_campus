package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScTextbook;

public interface IScTextbookService {
    ScTextbook selectScTextbookByTextbookId(Long textbookId);

    List<ScTextbook> selectScTextbookList(ScTextbook query);

    int insertScTextbook(ScTextbook scTextbook);

    int updateScTextbook(ScTextbook scTextbook);

    int deleteScTextbookByTextbookIds(Long[] textbookIds);
}
