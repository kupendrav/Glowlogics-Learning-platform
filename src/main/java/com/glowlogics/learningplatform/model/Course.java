package com.glowlogics.learningplatform.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Course {
    private final Long id;
    private final String slug;
    private final String title;
    private final String description;
    private final String category;
    private final String duration;
    private final String level;
    private final boolean featured;
    private final String imageUrl;
    private final String discordInviteUrl;
    private final List<String> roadmap;
    private final List<Lesson> lessons;

    public Course(Long id,
                  String slug,
                  String title,
                  String description,
                  String category,
                  String duration,
                  String level,
                  boolean featured,
                  String imageUrl,
                  String discordInviteUrl,
                  List<String> roadmap,
                  List<Lesson> lessons) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.category = category;
        this.duration = duration;
        this.level = level;
        this.featured = featured;
        this.imageUrl = imageUrl;
        this.discordInviteUrl = discordInviteUrl;
        this.roadmap = new ArrayList<>(roadmap);
        this.lessons = new ArrayList<>(lessons);
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDuration() {
        return duration;
    }

    public String getLevel() {
        return level;
    }

    public boolean isFeatured() {
        return featured;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDiscordInviteUrl() {
        return discordInviteUrl;
    }

    public List<String> getRoadmap() {
        return List.copyOf(roadmap);
    }

    public List<Lesson> getLessons() {
        return List.copyOf(lessons);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course course)) {
            return false;
        }
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
