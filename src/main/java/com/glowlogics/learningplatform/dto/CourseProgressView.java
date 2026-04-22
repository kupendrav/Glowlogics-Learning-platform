package com.glowlogics.learningplatform.dto;

public record CourseProgressView(
        Long courseId,
        String courseTitle,
        String courseSlug,
        int completedLessons,
        int totalLessons,
        int progressPercent,
        boolean enrolled
) {
}
