package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScExamPaper;
import com.smart.system.mapper.ScExamPaperMapper;
import com.smart.system.service.IScExamPaperService;

@Service
public class ScExamPaperServiceImpl implements IScExamPaperService
{
    @Autowired
    private ScExamPaperMapper scExamPaperMapper;
    public ScExamPaper selectScExamPaperByPaperId(Long paperId) { return scExamPaperMapper.selectScExamPaperByPaperId(paperId); }
    public List<ScExamPaper> selectScExamPaperList(ScExamPaper scExamPaper) { return scExamPaperMapper.selectScExamPaperList(scExamPaper); }
    public int insertScExamPaper(ScExamPaper scExamPaper) { return scExamPaperMapper.insertScExamPaper(scExamPaper); }
    public int updateScExamPaper(ScExamPaper scExamPaper) { return scExamPaperMapper.updateScExamPaper(scExamPaper); }
    public int deleteScExamPaperByPaperIds(Long[] paperIds) { return scExamPaperMapper.deleteScExamPaperByPaperIds(paperIds); }
    public int deleteScExamPaperByPaperId(Long paperId) { return scExamPaperMapper.deleteScExamPaperByPaperId(paperId); }
}
