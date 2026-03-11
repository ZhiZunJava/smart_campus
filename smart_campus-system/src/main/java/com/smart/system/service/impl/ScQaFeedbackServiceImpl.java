package com.smart.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.smart.system.domain.ScQaFeedback;
import com.smart.system.mapper.ScQaFeedbackMapper;
import com.smart.system.service.IScQaFeedbackService;

@Service
public class ScQaFeedbackServiceImpl implements IScQaFeedbackService
{
    @Autowired
    private ScQaFeedbackMapper scQaFeedbackMapper;

    public ScQaFeedback selectScQaFeedbackById(Long id) { return scQaFeedbackMapper.selectScQaFeedbackById(id); }
    public List<ScQaFeedback> selectScQaFeedbackList(ScQaFeedback scQaFeedback) { return scQaFeedbackMapper.selectScQaFeedbackList(scQaFeedback); }
    public int insertScQaFeedback(ScQaFeedback scQaFeedback) { return scQaFeedbackMapper.insertScQaFeedback(scQaFeedback); }
    public int updateScQaFeedback(ScQaFeedback scQaFeedback) { return scQaFeedbackMapper.updateScQaFeedback(scQaFeedback); }
    public int deleteScQaFeedbackByIds(Long[] ids) { return scQaFeedbackMapper.deleteScQaFeedbackByIds(ids); }
    public int deleteScQaFeedbackById(Long id) { return scQaFeedbackMapper.deleteScQaFeedbackById(id); }
}
