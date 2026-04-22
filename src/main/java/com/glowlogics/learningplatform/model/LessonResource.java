package com.glowlogics.learningplatform.model;

import java.util.Objects;

public class LessonResource {
    private final String title;
    private final String url;

    public LessonResource(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LessonResource that)) {
            return false;
        }
        return Objects.equals(title, that.title) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url);
    }
}
