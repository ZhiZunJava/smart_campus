package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScGrade;

public interface ScGradeMapper {
    ScGrade selectScGradeByGradeId(Long gradeId);

    List<ScGrade> selectScGradeList(ScGrade scGrade);

    int insertScGrade(ScGrade scGrade);

    int updateScGrade(ScGrade scGrade);

    int deleteScGradeByGradeId(Long gradeId);

    int deleteScGradeByGradeIds(Long[] gradeIds);
}
