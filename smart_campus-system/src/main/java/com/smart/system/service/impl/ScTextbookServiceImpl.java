package com.smart.system.service.impl;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScTextbook;
import com.smart.system.mapper.ScTextbookMapper;
import com.smart.system.service.IScTextbookService;

@Service
public class ScTextbookServiceImpl implements IScTextbookService {
    @Autowired
    private ScTextbookMapper scTextbookMapper;

    @Override
    public ScTextbook selectScTextbookByTextbookId(Long textbookId) {
        return scTextbookMapper.selectScTextbookByTextbookId(textbookId);
    }

    @Override
    public List<ScTextbook> selectScTextbookList(ScTextbook query) {
        return scTextbookMapper.selectScTextbookList(query);
    }

    @Override
    public int insertScTextbook(ScTextbook scTextbook) {
        if (StringUtils.isBlank(scTextbook.getTextbookCode())) {
            scTextbook.setTextbookCode(buildDefaultCode());
        }
        if (scTextbook.getStatus() == null) {
            scTextbook.setStatus("0");
        }
        return scTextbookMapper.insertScTextbook(scTextbook);
    }

    @Override
    public int updateScTextbook(ScTextbook scTextbook) {
        return scTextbookMapper.updateScTextbook(scTextbook);
    }

    @Override
    public int deleteScTextbookByTextbookIds(Long[] textbookIds) {
        return scTextbookMapper.deleteScTextbookByTextbookIds(textbookIds);
    }

    private String buildDefaultCode() {
        long suffix = System.currentTimeMillis() % 10000000000L;
        return "TB" + suffix;
    }
}
