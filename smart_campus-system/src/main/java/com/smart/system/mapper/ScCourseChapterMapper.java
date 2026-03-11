package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScCourseChapter;

public interface ScCourseChapterMapper
{
    ScCourseChapter selectScCourseChapterByChapterId(Long chapterId);
    List<ScCourseChapter> selectScCourseChapterList(ScCourseChapter scCourseChapter);
    int insertScCourseChapter(ScCourseChapter scCourseChapter);
    int updateScCourseChapter(ScCourseChapter scCourseChapter);
    int deleteScCourseChapterByChapterId(Long chapterId);
    int deleteScCourseChapterByChapterIds(Long[] chapterIds);
}
