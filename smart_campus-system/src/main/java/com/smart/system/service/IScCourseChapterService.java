package com.smart.system.service;

import java.util.List;
import com.smart.system.domain.ScCourseChapter;

public interface IScCourseChapterService {
    ScCourseChapter selectScCourseChapterByChapterId(Long chapterId);

    List<ScCourseChapter> selectScCourseChapterList(ScCourseChapter scCourseChapter);

    int insertScCourseChapter(ScCourseChapter scCourseChapter);

    int updateScCourseChapter(ScCourseChapter scCourseChapter);

    int deleteScCourseChapterByChapterIds(Long[] chapterIds);

    int deleteScCourseChapterByChapterId(Long chapterId);
}
