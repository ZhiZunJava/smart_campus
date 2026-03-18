package com.smart.system.domain.campusvo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseScheduleAutoArrangeVo {
    private Long termId;
    private String termName;
    private Integer totalLessonTasks;
    private Integer arrangedLessonTasks;
    private Integer insertedSchedules;
    private Integer clearedSchedules;
    private Integer populationSize;
    private Integer generationCount;
    private Double mutationRate;
    private Double bestFitnessScore;
    private List<String> warnings = new ArrayList<>();
    private List<Map<String, Object>> arrangedLessons = new ArrayList<>();
    private List<Map<String, Object>> unscheduledLessons = new ArrayList<>();

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Integer getTotalLessonTasks() {
        return totalLessonTasks;
    }

    public void setTotalLessonTasks(Integer totalLessonTasks) {
        this.totalLessonTasks = totalLessonTasks;
    }

    public Integer getArrangedLessonTasks() {
        return arrangedLessonTasks;
    }

    public void setArrangedLessonTasks(Integer arrangedLessonTasks) {
        this.arrangedLessonTasks = arrangedLessonTasks;
    }

    public Integer getInsertedSchedules() {
        return insertedSchedules;
    }

    public void setInsertedSchedules(Integer insertedSchedules) {
        this.insertedSchedules = insertedSchedules;
    }

    public Integer getClearedSchedules() {
        return clearedSchedules;
    }

    public void setClearedSchedules(Integer clearedSchedules) {
        this.clearedSchedules = clearedSchedules;
    }

    public Integer getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(Integer populationSize) {
        this.populationSize = populationSize;
    }

    public Integer getGenerationCount() {
        return generationCount;
    }

    public void setGenerationCount(Integer generationCount) {
        this.generationCount = generationCount;
    }

    public Double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(Double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public Double getBestFitnessScore() {
        return bestFitnessScore;
    }

    public void setBestFitnessScore(Double bestFitnessScore) {
        this.bestFitnessScore = bestFitnessScore;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<Map<String, Object>> getArrangedLessons() {
        return arrangedLessons;
    }

    public void setArrangedLessons(List<Map<String, Object>> arrangedLessons) {
        this.arrangedLessons = arrangedLessons;
    }

    public List<Map<String, Object>> getUnscheduledLessons() {
        return unscheduledLessons;
    }

    public void setUnscheduledLessons(List<Map<String, Object>> unscheduledLessons) {
        this.unscheduledLessons = unscheduledLessons;
    }
}
