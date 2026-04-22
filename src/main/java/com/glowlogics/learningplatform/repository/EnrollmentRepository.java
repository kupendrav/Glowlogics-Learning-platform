package com.glowlogics.learningplatform.repository;

import com.glowlogics.learningplatform.model.Enrollment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EnrollmentRepository {
    private final AtomicLong idSequence = new AtomicLong(1);
    private final Map<String, Enrollment> enrollmentByLearnerAndCourse = new ConcurrentHashMap<>();

    public Enrollment saveOrGet(String learnerId, Long courseId) {
        String key = key(learnerId, courseId);
        return enrollmentByLearnerAndCourse.computeIfAbsent(
                key,
                unused -> new Enrollment(idSequence.getAndIncrement(), learnerId, courseId, java.time.Instant.now())
        );
    }

    public Optional<Enrollment> findByLearnerAndCourse(String learnerId, Long courseId) {
        return Optional.ofNullable(enrollmentByLearnerAndCourse.get(key(learnerId, courseId)));
    }

    public List<Enrollment> findByLearner(String learnerId) {
        return enrollmentByLearnerAndCourse.values().stream()
                .filter(enrollment -> enrollment.getLearnerId().equalsIgnoreCase(learnerId))
                .toList();
    }

    private String key(String learnerId, Long courseId) {
        return learnerId.trim().toLowerCase() + "::" + courseId;
    }
}
