package com.glowlogics.learningplatform.controller;

import com.glowlogics.learningplatform.dto.EnrollmentRequest;
import com.glowlogics.learningplatform.dto.ProgressUpdateRequest;
import com.glowlogics.learningplatform.dto.SyncProgressRequest;
import com.glowlogics.learningplatform.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final EnrollmentService enrollmentService;

    public ApiController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enrollments")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> enroll(@Valid @RequestBody EnrollmentRequest request, Principal principal) {
        String learnerId = principal.getName();
        enrollmentService.enroll(learnerId, request.courseId());
        return Map.of("status", "enrolled", "learnerId", learnerId, "courseId", request.courseId());
    }

    @PostMapping("/progress")
    public Map<String, Object> completeLesson(@Valid @RequestBody ProgressUpdateRequest request, Principal principal) {
        String learnerId = principal.getName();
        boolean completed = request.completed() == null || request.completed();

        var result = enrollmentService.updateLessonCompletion(
                learnerId,
                request.courseId(),
                request.lessonId(),
                completed
        );

        return Map.of(
                "status", "updated",
                "courseId", result.courseId(),
                "progressPercent", result.progressPercent(),
                "completedLessons", result.completedLessons(),
                "totalLessons", result.totalLessons(),
                "completed", completed
        );
    }

    @PostMapping("/sync")
    public Map<String, Object> sync(@Valid @RequestBody SyncProgressRequest request, Principal principal) {
        String learnerId = principal.getName();
        var view = enrollmentService.syncProgress(learnerId, request.courseId(), request.completedByLessonId());
        return Map.of(
                "status", "synced",
                "courseId", view.courseId(),
                "progressPercent", view.progressPercent(),
                "completedLessons", view.completedLessons(),
                "totalLessons", view.totalLessons()
        );
    }
}
