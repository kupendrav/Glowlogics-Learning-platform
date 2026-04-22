package com.glowlogics.learningplatform.model;

import java.time.Instant;

public class LessonProgress {
    private final Long lessonId;
    private boolean completed;
    private Instant completedAt;

    public LessonProgress(Long lessonId, boolean completed, Instant completedAt) {
        this.lessonId = lessonId;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void markCompleted() {
        this.completed = true;
        this.completedAt = Instant.now();
    }

    public void markIncomplete() {
        this.completed = false;
        this.completedAt = null;
    }
}
