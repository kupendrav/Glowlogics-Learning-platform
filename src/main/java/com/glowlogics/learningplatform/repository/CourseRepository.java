package com.glowlogics.learningplatform.repository;

import com.glowlogics.learningplatform.model.Course;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CourseRepository {
    private final AtomicLong idSequence = new AtomicLong(1);
    private final Map<Long, Course> coursesById = new ConcurrentHashMap<>();

    public void saveAll(List<Course> courses) {
        courses.forEach(this::save);
    }

    public Course save(Course course) {
        coursesById.put(course.getId(), course);
        idSequence.updateAndGet(current -> Math.max(current, course.getId() + 1));
        return course;
    }

    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>(coursesById.values());
        courses.sort(Comparator.comparing(Course::getId));
        return courses;
    }

    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(coursesById.get(id));
    }

    public Optional<Course> findBySlug(String slug) {
        return coursesById.values().stream()
                .filter(course -> course.getSlug().equalsIgnoreCase(slug))
                .findFirst();
    }

    public Long nextId() {
        return idSequence.getAndIncrement();
    }
}
