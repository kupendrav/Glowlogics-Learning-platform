package com.glowlogics.learningplatform.dto;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull(message = "courseId is required")
        Long courseId
) {
}
