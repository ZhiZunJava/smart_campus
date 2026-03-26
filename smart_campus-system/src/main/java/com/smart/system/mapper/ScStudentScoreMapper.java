package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScStudentScore;

public interface ScStudentScoreMapper {
    ScStudentScore selectScStudentScoreByScoreId(Long scoreId);

    List<ScStudentScore> selectScStudentScoreList(ScStudentScore scStudentScore);

    int insertScStudentScore(ScStudentScore scStudentScore);

    int updateScStudentScore(ScStudentScore scStudentScore);

    int deleteScStudentScoreByScoreId(Long scoreId);

    int deleteScStudentScoreByScoreIds(Long[] scoreIds);
}
