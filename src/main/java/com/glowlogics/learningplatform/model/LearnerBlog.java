package com.glowlogics.learningplatform.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class LearnerBlog {
    private final Long id;
    private final String slug;
    private final String title;
    private final String summary;
    private final String coverImageUrl;
    private final String contentMarkdown;
    private final String contentHtml;
    private final String author;
    private final LocalDateTime createdAt;

    public LearnerBlog(Long id,
                       String slug,
                       String title,
                       String summary,
                       String coverImageUrl,
                       String contentMarkdown,
                       String contentHtml,
                       String author,
                       LocalDateTime createdAt) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.summary = summary;
        this.coverImageUrl = coverImageUrl;
        this.contentMarkdown = contentMarkdown;
        this.contentHtml = contentHtml;
        this.author = author;
        this.createdAt = createdAt;
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

    public String getSummary() {
        return summary;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getContentMarkdown() {
        return contentMarkdown;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LearnerBlog that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
