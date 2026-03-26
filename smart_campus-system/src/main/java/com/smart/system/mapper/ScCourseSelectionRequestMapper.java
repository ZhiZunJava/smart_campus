package com.smart.system.mapper;

import java.util.List;
import com.smart.system.domain.ScCourseSelectionRequest;

public interface ScCourseSelectionRequestMapper {
    ScCourseSelectionRequest selectScCourseSelectionRequestByRequestId(Long requestId);

    List<ScCourseSelectionRequest> selectScCourseSelectionRequestList(ScCourseSelectionRequest request);

    ScCourseSelectionRequest selectPendingByStudentAndClassCourse(ScCourseSelectionRequest request);

    int insertScCourseSelectionRequest(ScCourseSelectionRequest request);

    int updateScCourseSelectionRequest(ScCourseSelectionRequest request);

    int deleteScCourseSelectionRequestByRequestId(Long requestId);

    int deleteScCourseSelectionRequestByRequestIds(Long[] requestIds);
}
