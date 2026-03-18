package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScCourseChapter;
import com.smart.system.mapper.ScCourseChapterMapper;
import com.smart.system.service.IScCourseChapterService;

@Service
public class ScCourseChapterServiceImpl implements IScCourseChapterService {
    @Autowired
    private ScCourseChapterMapper scCourseChapterMapper;

    public ScCourseChapter selectScCourseChapterByChapterId(Long chapterId) {
        return scCourseChapterMapper.selectScCourseChapterByChapterId(chapterId);
    }

    public List<ScCourseChapter> selectScCourseChapterList(ScCourseChapter scCourseChapter) {
        return scCourseChapterMapper.selectScCourseChapterList(scCourseChapter);
    }

    public int insertScCourseChapter(ScCourseChapter scCourseChapter) {
        return scCourseChapterMapper.insertScCourseChapter(scCourseChapter);
    }

    public int updateScCourseChapter(ScCourseChapter scCourseChapter) {
        return scCourseChapterMapper.updateScCourseChapter(scCourseChapter);
    }

    public int deleteScCourseChapterByChapterIds(Long[] chapterIds) {
        return scCourseChapterMapper.deleteScCourseChapterByChapterIds(chapterIds);
    }

    public int deleteScCourseChapterByChapterId(Long chapterId) {
        return scCourseChapterMapper.deleteScCourseChapterByChapterId(chapterId);
    }
}
