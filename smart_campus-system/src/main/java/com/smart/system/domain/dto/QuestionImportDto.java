package com.smart.system.domain.dto;

import com.smart.common.annotation.Excel;

/**
 * 题库导入 DTO
 */
public class QuestionImportDto {
    @Excel(name = "课程ID", cellType = Excel.ColumnType.NUMERIC)
    private Long courseId;

    @Excel(name = "知识点ID", cellType = Excel.ColumnType.NUMERIC)
    private Long knowledgePointId;

    @Excel(name = "题型")
    private String questionType;

    @Excel(name = "难度", cellType = Excel.ColumnType.NUMERIC)
    private Integer difficultyLevel;

    @Excel(name = "题干")
    private String stem;

    @Excel(name = "选项")
    private String optionsText;

    @Excel(name = "答案")
    private String answer;

    @Excel(name = "解析")
    private String analysis;

    @Excel(name = "来源")
    private String source;

    @Excel(name = "标签")
    private String questionTags;

    @Excel(name = "材料")
    private String materialContent;

    @Excel(name = "来源批次")
    private String sourceBatchNo;

    @Excel(name = "状态")
    private String status;

    @Excel(name = "备注")
    private String remark;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(Long knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getOptionsText() {
        return optionsText;
    }

    public void setOptionsText(String optionsText) {
        this.optionsText = optionsText;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQuestionTags() {
        return questionTags;
    }

    public void setQuestionTags(String questionTags) {
        this.questionTags = questionTags;
    }

    public String getMaterialContent() {
        return materialContent;
    }

    public void setMaterialContent(String materialContent) {
        this.materialContent = materialContent;
    }

    public String getSourceBatchNo() {
        return sourceBatchNo;
    }

    public void setSourceBatchNo(String sourceBatchNo) {
        this.sourceBatchNo = sourceBatchNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
