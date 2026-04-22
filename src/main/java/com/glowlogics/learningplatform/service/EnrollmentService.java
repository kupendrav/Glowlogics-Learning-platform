package com.glowlogics.learningplatform.service;

import com.glowlogics.learningplatform.dto.CourseProgressView;
import com.glowlogics.learningplatform.model.Course;
import com.glowlogics.learningplatform.model.Enrollment;
import com.glowlogics.learningplatform.model.Lesson;
import com.glowlogics.learningplatform.model.LessonProgress;
import com.glowlogics.learningplatform.repository.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {
    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseService = courseService;
    }

    public Enrollment enroll(String learnerId, Long courseId) {
        Course course = courseService.getById(courseId);
        Enrollment enrollment = enrollmentRepository.saveOrGet(learnerId, courseId);

        for (Lesson lesson : course.getLessons()) {
            enrollment.getProgressByLessonId()
                    .computeIfAbsent(lesson.getId(), lessonId -> new LessonProgress(lessonId, false, null));
        }
        log.info("Learner {} enrolled in course {}", learnerId, courseId);
        return enrollment;
    }

    public CourseProgressView completeLesson(String learnerId, Long courseId, Long lessonId) {
        return updateLessonCompletion(learnerId, courseId, lessonId, true);
    }

    public CourseProgressView updateLessonCompletion(String learnerId, Long courseId, Long lessonId, boolean completed) {
        Course course = courseService.getById(courseId);
        Set<Long> lessonIds = getCourseLessonIds(course);
        if (!lessonIds.contains(lessonId)) {
            throw new ResourceNotFoundException("Lesson " + lessonId + " does not belong to course " + courseId);
        }

        Enrollment enrollment = enrollmentRepository.findByLearnerAndCourse(learnerId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found. Enroll first."));

        LessonProgress progress = enrollment.getProgressByLessonId()
                .computeIfAbsent(lessonId, id -> new LessonProgress(id, false, null));

        if (completed && !progress.isCompleted()) {
            progress.markCompleted();
            log.info("Learner {} completed lesson {} in course {}", learnerId, lessonId, courseId);
        }
        if (!completed && progress.isCompleted()) {
            progress.markIncomplete();
            log.info("Learner {} set lesson {} as incomplete in course {}", learnerId, lessonId, courseId);
        }

        return toProgressView(course, enrollment);
    }

    public List<CourseProgressView> dashboard(String learnerId) {
        return enrollmentRepository.findByLearner(learnerId).stream()
                .map(enrollment -> {
                    Course course = courseService.getById(enrollment.getCourseId());
                    return toProgressView(course, enrollment);
                })
                .sorted(Comparator.comparing(CourseProgressView::courseId))
                .toList();
    }

    public CourseProgressView syncProgress(String learnerId, Long courseId, Map<Long, Boolean> completedByLessonId) {
        Course course = courseService.getById(courseId);
        Enrollment enrollment = enrollmentRepository.findByLearnerAndCourse(learnerId, courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found. Enroll first."));
        Set<Long> lessonIds = getCourseLessonIds(course);

        Map<Long, Boolean> safeCompletedByLessonId = completedByLessonId == null ? Map.of() : completedByLessonId;

        for (Map.Entry<Long, Boolean> entry : safeCompletedByLessonId.entrySet()) {
            if (lessonIds.contains(entry.getKey())) {
                LessonProgress progress = enrollment.getProgressByLessonId()
                        .computeIfAbsent(entry.getKey(), id -> new LessonProgress(id, false, null));

                if (Boolean.TRUE.equals(entry.getValue()) && !progress.isCompleted()) {
                    progress.markCompleted();
                }
                if (!Boolean.TRUE.equals(entry.getValue()) && progress.isCompleted()) {
                    progress.markIncomplete();
                }
            }
        }
        return toProgressView(course, enrollment);
    }

    public CourseProgressView getCourseProgress(String learnerId, Long courseId) {
        Course course = courseService.getById(courseId);
        Enrollment enrollment = enrollmentRepository.findByLearnerAndCourse(learnerId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for learner and course."));
        return toProgressView(course, enrollment);
    }

    public boolean isCourseCompleted(String learnerId, Long courseId) {
        CourseProgressView progressView = getCourseProgress(learnerId, courseId);
        return progressView.totalLessons() > 0 && progressView.completedLessons() == progressView.totalLessons();
    }

    private CourseProgressView toProgressView(Course course, Enrollment enrollment) {
        Set<Long> lessonIds = getCourseLessonIds(course);
        int totalLessons = course.getLessons().size();
        long completedLessons = enrollment.getProgressByLessonId().entrySet().stream()
            .filter(entry -> lessonIds.contains(entry.getKey()))
            .map(Map.Entry::getValue)
            .filter(LessonProgress::isCompleted)
                .count();
        int progressPercent = totalLessons == 0 ? 0 : (int) Math.round((completedLessons * 100.0) / totalLessons);

        return new CourseProgressView(
                course.getId(),
                course.getTitle(),
                course.getSlug(),
                (int) completedLessons,
                totalLessons,
                progressPercent,
                true
        );
    }

    private Set<Long> getCourseLessonIds(Course course) {
        return course.getLessons().stream().map(Lesson::getId).collect(Collectors.toSet());
    }
}
