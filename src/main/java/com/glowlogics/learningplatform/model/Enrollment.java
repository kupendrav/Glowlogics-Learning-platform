package com.glowlogics.learningplatform.model;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Enrollment {
    private final Long id;
    private final String learnerId;
    private final Long courseId;
    private final Instant enrolledAt;
    private final Map<Long, LessonProgress> progressByLessonId = new ConcurrentHashMap<>();

    public Enrollment(Long id, String learnerId, Long courseId, Instant enrolledAt) {
        this.id = id;
        this.learnerId = learnerId;
        this.courseId = courseId;
        this.enrolledAt = enrolledAt;
    }

    public Long getId() {
        return id;
    }

    public String getLearnerId() {
        return learnerId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public Map<Long, LessonProgress> getProgressByLessonId() {
        return progressByLessonId;
    }
}
