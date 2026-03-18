package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScGrade;

public interface IScGradeService {
    ScGrade selectScGradeByGradeId(Long gradeId);

    List<ScGrade> selectScGradeList(ScGrade scGrade);

    int insertScGrade(ScGrade scGrade);

    int updateScGrade(ScGrade scGrade);

    int deleteScGradeByGradeIds(Long[] gradeIds);

    int deleteScGradeByGradeId(Long gradeId);
}
