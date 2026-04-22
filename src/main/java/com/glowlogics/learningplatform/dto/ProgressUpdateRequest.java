package com.glowlogics.learningplatform.dto;

import jakarta.validation.constraints.NotNull;

public record ProgressUpdateRequest(
        @NotNull(message = "courseId is required")
        Long courseId,
        @NotNull(message = "lessonId is required")
        Long lessonId,
        Boolean completed
) {
}
