package com.smart.system.service;

import java.util.List;
import java.util.Map;
import com.smart.system.domain.ScCourseSelectionRequest;
import com.smart.system.domain.dto.CourseSelectionRequestReviewDto;

public interface IScCourseSelectionRequestService {
    ScCourseSelectionRequest selectScCourseSelectionRequestByRequestId(Long requestId);

    List<ScCourseSelectionRequest> selectScCourseSelectionRequestList(ScCourseSelectionRequest request);

    Map<String, Object> createRequest(ScCourseSelectionRequest request, String operator);

    Map<String, Object> cancelRequest(Long requestId, Long studentUserId, String operator);

    Map<String, Object> reviewRequest(CourseSelectionRequestReviewDto dto, String operator, Long reviewerUserId);
}
