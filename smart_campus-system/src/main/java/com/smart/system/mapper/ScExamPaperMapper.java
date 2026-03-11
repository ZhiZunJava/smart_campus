package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamPaper;

public interface ScExamPaperMapper
{
    ScExamPaper selectScExamPaperByPaperId(Long paperId);
    List<ScExamPaper> selectScExamPaperList(ScExamPaper scExamPaper);
    int insertScExamPaper(ScExamPaper scExamPaper);
    int updateScExamPaper(ScExamPaper scExamPaper);
    int deleteScExamPaperByPaperId(Long paperId);
    int deleteScExamPaperByPaperIds(Long[] paperIds);
}
