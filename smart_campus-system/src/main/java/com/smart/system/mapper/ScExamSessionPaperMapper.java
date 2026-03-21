package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScExamSessionPaper;

public interface ScExamSessionPaperMapper {
    ScExamSessionPaper selectScExamSessionPaperById(Long id);

    ScExamSessionPaper selectScExamSessionPaperBySessionAndPaper(Long sessionId, Long paperId);

    List<ScExamSessionPaper> selectScExamSessionPaperList(ScExamSessionPaper scExamSessionPaper);

    int insertScExamSessionPaper(ScExamSessionPaper scExamSessionPaper);

    int updateScExamSessionPaper(ScExamSessionPaper scExamSessionPaper);
}

