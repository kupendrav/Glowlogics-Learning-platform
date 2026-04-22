package com.glowlogics.learningplatform.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.glowlogics.learningplatform.model.Course;
import com.glowlogics.learningplatform.model.Lesson;
import com.glowlogics.learningplatform.model.LessonResource;
import com.glowlogics.learningplatform.repository.CourseRepository;

@Service
public class CourseService {

    private static final String DEFAULT_DISCORD_URL = "https://discord.gg/uuqdBbgc";
    private static final String FALLBACK_COURSE_IMAGE = "https://images.pexels.com/photos/177598/pexels-photo-177598.jpeg?auto=compress&cs=tinysrgb&w=1200";

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll(String query, String category) {
        List<Course> courses = courseRepository.findAll();
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        String normalizedCategory = category == null ? "" : category.trim().toLowerCase(Locale.ROOT);

        return courses.stream()
                .filter(course -> normalizedCategory.isBlank()
                        || "all".equals(normalizedCategory)
                        || course.getCategory().toLowerCase(Locale.ROOT).contains(normalizedCategory))
                .filter(course -> normalizedQuery.isBlank() || matchesQuery(course, normalizedQuery))
                .toList();
    }

    public List<Course> featuredCourses() {
        return courseRepository.findAll().stream()
                .filter(Course::isFeatured)
                .limit(4)
                .toList();
    }

    public Course getById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found for id: " + courseId));
    }

    public Course getBySlug(String slug) {
        return courseRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found for slug: " + slug));
    }

    public Course createCourse(String title,
                               String description,
                               String category,
                               String duration,
                               String level,
                               boolean featured) {
        Long id = courseRepository.nextId();
        String baseSlug = slugify(title);
        String uniqueSlug = ensureUniqueSlug(baseSlug, null);

        Course course = new Course(
                id,
                uniqueSlug,
                title.trim(),
                description.trim(),
                category.trim(),
                duration.trim(),
                level.trim(),
                featured,
                resolveDefaultImageForCategory(category),
                DEFAULT_DISCORD_URL,
                List.of(
                        "Start with fundamentals",
                        "Build practical mini projects",
                        "Capstone and interview preparation"
                ),
                List.of(
                        new Lesson(
                                id * 100 + 1,
                                "Introduction and Learning Plan",
                            "https://www.youtube.com/embed/9SGDpanrc8U",
                                "This placeholder lesson can be replaced from admin tools after course creation.",
                                List.of(
                                        new LessonResource("Official Documentation", "https://developer.mozilla.org/"),
                                        new LessonResource("Community Notes", "https://github.com/"))
                        )
                )
        );

        return courseRepository.save(course);
    }

    public Course updateCourse(Long id,
                               String title,
                               String description,
                               String category,
                               String duration,
                               String level,
                               boolean featured) {
        Course existing = getById(id);
        String baseSlug = slugify(title);
        String uniqueSlug = ensureUniqueSlug(baseSlug, id);

        Course updated = new Course(
                existing.getId(),
                uniqueSlug,
                title.trim(),
                description.trim(),
                category.trim(),
                duration.trim(),
                level.trim(),
                featured,
                existing.getImageUrl(),
                existing.getDiscordInviteUrl(),
                existing.getRoadmap(),
                existing.getLessons()
        );

        return courseRepository.save(updated);
    }

    private boolean matchesQuery(Course course, String query) {
        return course.getTitle().toLowerCase(Locale.ROOT).contains(query)
                || course.getDescription().toLowerCase(Locale.ROOT).contains(query)
                || course.getCategory().toLowerCase(Locale.ROOT).contains(query)
                || course.getDuration().toLowerCase(Locale.ROOT).contains(query)
                || course.getLevel().toLowerCase(Locale.ROOT).contains(query);
    }

    private String slugify(String raw) {
        String slug = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        slug = slug.replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("^-+|-+$", "");
        return slug.isBlank() ? "course" : slug;
    }

    private String ensureUniqueSlug(String baseSlug, Long currentCourseId) {
        String candidate = baseSlug;
        int suffix = 2;

        while (true) {
            Optional<Course> found = courseRepository.findBySlug(candidate);
            if (found.isEmpty() || (currentCourseId != null && found.get().getId().equals(currentCourseId))) {
                return candidate;
            }
            candidate = baseSlug + "-" + suffix;
            suffix++;
        }
    }

    private String resolveDefaultImageForCategory(String category) {
        String normalized = category == null ? "" : category.trim().toLowerCase(Locale.ROOT);

        if (normalized.contains("data") || normalized.contains("analytics") || normalized.contains("bi")) {
            return "https://images.pexels.com/photos/5466250/pexels-photo-5466250.jpeg?auto=compress&cs=tinysrgb&w=1200";
        }
        if (normalized.contains("web")) {
            return "https://images.pexels.com/photos/14553720/pexels-photo-14553720.jpeg?auto=compress&cs=tinysrgb&w=1200";
        }
        if (normalized.contains("mobile") || normalized.contains("app")) {
            return "https://images.pexels.com/photos/48606/pexels-photo-48606.jpeg?auto=compress&cs=tinysrgb&w=1200";
        }
        if (normalized.contains("security")) {
            return "https://images.pexels.com/photos/5483248/pexels-photo-5483248.jpeg?auto=compress&cs=tinysrgb&w=1200";
        }
        if (normalized.contains("cloud")) {
            return "https://images.pexels.com/photos/5475760/pexels-photo-5475760.jpeg?auto=compress&cs=tinysrgb&w=1200";
        }
        if (normalized.contains("iot") || normalized.contains("embedded") || normalized.contains("hardware") || normalized.contains("vlsi")) {
            return "https://images.pexels.com/photos/6755081/pexels-photo-6755081.jpeg?auto=compress&cs=tinysrgb&w=1200";
        }
        return FALLBACK_COURSE_IMAGE;
    }
}
