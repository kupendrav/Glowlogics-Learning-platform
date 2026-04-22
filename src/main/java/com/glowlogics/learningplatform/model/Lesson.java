package com.glowlogics.learningplatform.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lesson {
    private final Long id;
    private final String title;
    private final String videoUrl;
    private final String notes;
    private final List<LessonResource> resources;

    public Lesson(Long id, String title, String videoUrl, String notes, List<LessonResource> resources) {
        this.id = id;
        this.title = title;
        this.videoUrl = videoUrl;
        this.notes = notes;
        this.resources = new ArrayList<>(resources);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getNotes() {
        return notes;
    }

    public List<LessonResource> getResources() {
        return List.copyOf(resources);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lesson lesson)) {
            return false;
        }
        return Objects.equals(id, lesson.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
