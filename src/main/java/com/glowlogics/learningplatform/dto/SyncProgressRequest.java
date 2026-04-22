package com.glowlogics.learningplatform.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record SyncProgressRequest(
        @NotNull(message = "courseId is required")
        Long courseId,
        Map<Long, Boolean> completedByLessonId
) {
}
