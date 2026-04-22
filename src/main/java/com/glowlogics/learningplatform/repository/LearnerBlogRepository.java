package com.glowlogics.learningplatform.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.glowlogics.learningplatform.model.LearnerBlog;

@Repository
public class LearnerBlogRepository {
    private final AtomicLong idSequence = new AtomicLong(1);
    private final Map<Long, LearnerBlog> blogsById = new ConcurrentHashMap<>();

    public LearnerBlog save(LearnerBlog blog) {
        blogsById.put(blog.getId(), blog);
        idSequence.updateAndGet(current -> Math.max(current, blog.getId() + 1));
        return blog;
    }

    public List<LearnerBlog> findAll() {
        List<LearnerBlog> blogs = new ArrayList<>(blogsById.values());
        blogs.sort(
                Comparator.comparing(LearnerBlog::getCreatedAt)
                        .reversed()
                        .thenComparing(LearnerBlog::getId, Comparator.reverseOrder())
        );
        return blogs;
    }

    public Optional<LearnerBlog> findBySlug(String slug) {
        return blogsById.values().stream()
                .filter(blog -> blog.getSlug().equalsIgnoreCase(slug))
                .findFirst();
    }

    public Long nextId() {
        return idSequence.getAndIncrement();
    }
}
