package com.glowlogics.learningplatform.service;

import com.glowlogics.learningplatform.dto.CourseProgressView;
import com.glowlogics.learningplatform.model.Enrollment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EnrollmentServiceTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Test
    void enrollShouldBeIdempotentForSameLearnerAndCourse() {
        Enrollment first = enrollmentService.enroll("learner-alpha", 1L);
        Enrollment second = enrollmentService.enroll("learner-alpha", 1L);

        assertThat(first.getId()).isEqualTo(second.getId());
    }

    @Test
    void completeLessonShouldCalculateProgressCorrectly() {
        enrollmentService.enroll("learner-beta", 1L);
        CourseProgressView progress = enrollmentService.completeLesson("learner-beta", 1L, 101L);

        assertThat(progress.completedLessons()).isEqualTo(1);
        assertThat(progress.totalLessons()).isEqualTo(3);
        assertThat(progress.progressPercent()).isEqualTo(33);
    }

    @Test
    void dashboardShouldReturnProgressForLearner() {
        enrollmentService.enroll("learner-gamma", 2L);
        enrollmentService.completeLesson("learner-gamma", 2L, 201L);

        List<CourseProgressView> dashboard = enrollmentService.dashboard("learner-gamma");
        assertThat(dashboard).hasSize(1);
        assertThat(dashboard.get(0).courseId()).isEqualTo(2L);
        assertThat(dashboard.get(0).progressPercent()).isEqualTo(33);
    }

    @Test
    void syncShouldIgnoreLessonIdsOutsideCourse() {
        enrollmentService.enroll("learner-delta", 1L);
        CourseProgressView progress = enrollmentService.syncProgress(
                "learner-delta",
                1L,
                Map.of(101L, true, 999L, true)
        );

        assertThat(progress.completedLessons()).isEqualTo(1);
        assertThat(progress.totalLessons()).isEqualTo(3);
        assertThat(progress.progressPercent()).isEqualTo(33);
    }
}
