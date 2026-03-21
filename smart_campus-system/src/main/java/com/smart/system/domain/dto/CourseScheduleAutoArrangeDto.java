package com.smart.system.domain.dto;

public class CourseScheduleAutoArrangeDto {
    private Long termId;
    private Boolean clearExistingSchedules;
    private Integer populationSize;
    private Integer generationCount;
    private Double mutationRate;
    private Long[] classCourseIds;
    private Integer[] preferredSessionDurations;
    private String[] excludedDayParts;
    private Integer[] excludedWeekDays;
    private CourseScheduleAutoArrangeItemDto[] items;

    public Long getTermId() {
        return termId;
    }

    public void setTermId(Long termId) {
        this.termId = termId;
    }

    public Boolean getClearExistingSchedules() {
        return clearExistingSchedules;
    }

    public void setClearExistingSchedules(Boolean clearExistingSchedules) {
        this.clearExistingSchedules = clearExistingSchedules;
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

    public Long[] getClassCourseIds() {
        return classCourseIds;
    }

    public void setClassCourseIds(Long[] classCourseIds) {
        this.classCourseIds = classCourseIds;
    }

    public Integer[] getPreferredSessionDurations() {
        return preferredSessionDurations;
    }

    public void setPreferredSessionDurations(Integer[] preferredSessionDurations) {
        this.preferredSessionDurations = preferredSessionDurations;
    }

    public String[] getExcludedDayParts() {
        return excludedDayParts;
    }

    public void setExcludedDayParts(String[] excludedDayParts) {
        this.excludedDayParts = excludedDayParts;
    }

    public Integer[] getExcludedWeekDays() {
        return excludedWeekDays;
    }

    public void setExcludedWeekDays(Integer[] excludedWeekDays) {
        this.excludedWeekDays = excludedWeekDays;
    }

    public CourseScheduleAutoArrangeItemDto[] getItems() {
        return items;
    }

    public void setItems(CourseScheduleAutoArrangeItemDto[] items) {
        this.items = items;
    }

    public static class CourseScheduleAutoArrangeItemDto {
        private Long classCourseId;
        private String weeksText;
        private String weeksJson;
        private Integer maxWeeklySections;
        private Long classroomId;
        private Integer[] excludedWeekDays;
        private String[] excludedDayParts;

        public Long getClassCourseId() {
            return classCourseId;
        }

        public void setClassCourseId(Long classCourseId) {
            this.classCourseId = classCourseId;
        }

        public String getWeeksText() {
            return weeksText;
        }

        public void setWeeksText(String weeksText) {
            this.weeksText = weeksText;
        }

        public String getWeeksJson() {
            return weeksJson;
        }

        public void setWeeksJson(String weeksJson) {
            this.weeksJson = weeksJson;
        }

        public Integer getMaxWeeklySections() {
            return maxWeeklySections;
        }

        public void setMaxWeeklySections(Integer maxWeeklySections) {
            this.maxWeeklySections = maxWeeklySections;
        }

        public Long getClassroomId() {
            return classroomId;
        }

        public void setClassroomId(Long classroomId) {
            this.classroomId = classroomId;
        }

        public Integer[] getExcludedWeekDays() {
            return excludedWeekDays;
        }

        public void setExcludedWeekDays(Integer[] excludedWeekDays) {
            this.excludedWeekDays = excludedWeekDays;
        }

        public String[] getExcludedDayParts() {
            return excludedDayParts;
        }

        public void setExcludedDayParts(String[] excludedDayParts) {
            this.excludedDayParts = excludedDayParts;
        }
    }
}
