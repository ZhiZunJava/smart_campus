package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScExamPaper;

public interface IScExamPaperService
{
    ScExamPaper selectScExamPaperByPaperId(Long paperId);
    List<ScExamPaper> selectScExamPaperList(ScExamPaper scExamPaper);
    int insertScExamPaper(ScExamPaper scExamPaper);
    int updateScExamPaper(ScExamPaper scExamPaper);
    int deleteScExamPaperByPaperIds(Long[] paperIds);
    int deleteScExamPaperByPaperId(Long paperId);
}
