package com.smart.system.domain.campusvo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 题目详情 VO
 *
 * @author Codex
 */
public class QuestionDetailVo
{
    private Long questionId;
    private Long courseId;
    private Long chapterId;
    private Long knowledgePointId;
    private String questionType;
    private Integer difficultyLevel;
    private String stem;
    private String answer;
    private String analysis;
    private String source;
    private BigDecimal qualityScore;
    private String status;
    private List<QuestionOptionVo> options;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    public Long getKnowledgePointId() { return knowledgePointId; }
    public void setKnowledgePointId(Long knowledgePointId) { this.knowledgePointId = knowledgePointId; }
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public Integer getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Integer difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<QuestionOptionVo> getOptions() { return options; }
    public void setOptions(List<QuestionOptionVo> options) { this.options = options; }
}
