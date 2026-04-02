package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScStudentScore;
import com.smart.system.domain.dto.ScStudentScoreImportDto;
import com.smart.system.domain.dto.ScStudentScorePublishDto;

public interface IScStudentScoreService {
    ScStudentScore selectScStudentScoreByScoreId(Long scoreId);

    List<ScStudentScore> selectScStudentScoreList(ScStudentScore scStudentScore);

    int insertScStudentScore(ScStudentScore scStudentScore);

    int updateScStudentScore(ScStudentScore scStudentScore);

    int deleteScStudentScoreByScoreIds(Long[] scoreIds);

    int deleteScStudentScoreByScoreId(Long scoreId);

    Map<String, Object> buildScoreOverview(ScStudentScore query);

    Map<String, Object> initScoresByClassCourse(Long classCourseId, String operator);

    Map<String, Object> syncExamScores(Long classCourseId, String operator);

    Map<String, Object> publishScores(ScStudentScorePublishDto dto, String operator);

    Map<String, Object> saveBatchScores(List<ScStudentScore> scores, String operator);

    Map<String, Object> importStudentScores(Long classCourseId, List<ScStudentScoreImportDto> rows, boolean updateSupport, String operator);

    List<ScStudentScoreImportDto> buildImportTemplateRows(Long classCourseId);

    List<ScStudentScore> selectPortalStudentScoreList(Long userId, Long termId, Long courseId);

    Map<String, Object> buildPortalScoreOverview(Long userId, Long termId);

    Map<String, Object> buildPortalScoreAnalytics(Long userId, Long termId, Long courseId);

    Map<String, Object> buildPortalScoreDetail(Long userId, Long classCourseId);
}
