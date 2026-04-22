package com.glowlogics.learningplatform.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.glowlogics.learningplatform.dto.CourseProgressView;
import com.glowlogics.learningplatform.model.Course;
import com.glowlogics.learningplatform.model.LearnerBlog;
import com.glowlogics.learningplatform.service.CourseService;
import com.glowlogics.learningplatform.service.EnrollmentService;
import com.glowlogics.learningplatform.service.LearnerBlogService;
import com.glowlogics.learningplatform.service.ResourceNotFoundException;

@Controller
public class WebController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final LearnerBlogService learnerBlogService;

    public WebController(CourseService courseService,
                         EnrollmentService enrollmentService,
                         LearnerBlogService learnerBlogService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.learnerBlogService = learnerBlogService;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        populateCommonModel(authentication, model);
        model.addAttribute("featuredCourses", courseService.featuredCourses());
        model.addAttribute("learnerBlogs", learnerBlogService.latestBlogs(6));
        model.addAttribute("courseCount", courseService.findAll("", "").size());
        return "pages/index";
    }

    @GetMapping("/courses")
    public String courseListing(@RequestParam(defaultValue = "") String q,
                                @RequestParam(defaultValue = "") String category,
                                Authentication authentication,
                                Model model) {
        populateCommonModel(authentication, model);
        model.addAttribute("query", q);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("courses", courseService.findAll(q, category));
        model.addAttribute("categories", List.of(
                "Software Engineering",
                "Data",
                "Web",
                "Security",
                "Mobile",
                "Programming",
                "Cloud",
                "Embedded",
                "Analytics",
                "Hardware"
        ));
        return "pages/courses";
    }

    @GetMapping("/courses/{slug}")
    public String courseDetail(@PathVariable String slug,
                               Authentication authentication,
                               Model model) {
        Course course = courseService.getBySlug(slug);

        populateCommonModel(authentication, model);
        model.addAttribute("course", course);
        model.addAttribute("currentProgress", 0);

        if (isAuthenticated(authentication)) {
            try {
                CourseProgressView progress = enrollmentService.getCourseProgress(authentication.getName(), course.getId());
                model.addAttribute("currentProgress", progress.progressPercent());
            } catch (ResourceNotFoundException ignored) {
                model.addAttribute("currentProgress", 0);
            }
        }

        return "pages/course-detail";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        populateCommonModel(authentication, model);

        String learner = authentication.getName();
        List<CourseProgressView> progressViews = enrollmentService.dashboard(learner);
        model.addAttribute("progressViews", progressViews);
        model.addAttribute("completedCourseCount", progressViews.stream().filter(view -> view.progressPercent() == 100).count());
        return "pages/dashboard";
    }

    @GetMapping("/certificates/{courseId}")
    public String certificate(@PathVariable Long courseId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            if (!enrollmentService.isCourseCompleted(authentication.getName(), courseId)) {
                redirectAttributes.addFlashAttribute("dashboardError", "Complete all lessons to unlock your certificate.");
                return "redirect:/dashboard";
            }
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("dashboardError", "Complete all lessons to unlock your certificate.");
            return "redirect:/dashboard";
        }

        populateCommonModel(authentication, model);
        Course course = courseService.getById(courseId);
        model.addAttribute("course", course);
        model.addAttribute("issuedDate", LocalDate.now());
        model.addAttribute("learnerName", authentication.getName());
        return "pages/certificate";
    }

    @GetMapping("/learner/blog/write")
    public String writeBlog(Authentication authentication, Model model) {
        populateCommonModel(authentication, model);

        if (!model.containsAttribute("blogTitle")) {
            model.addAttribute("blogTitle", "");
        }
        if (!model.containsAttribute("blogCoverImage")) {
            model.addAttribute("blogCoverImage", "");
        }
        if (!model.containsAttribute("blogContent")) {
            model.addAttribute("blogContent", "");
        }

        return "pages/learner-blog-write";
    }

    @PostMapping("/learner/blog")
    public String publishBlog(@RequestParam String title,
                              @RequestParam String content,
                              @RequestParam(required = false) String coverImage,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            LearnerBlog blog = learnerBlogService.createBlog(authentication.getName(), title, content, coverImage);
            redirectAttributes.addFlashAttribute("blogSuccess", "Blog published successfully.");
            return "redirect:/learner/blog/" + blog.getSlug();
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("blogError", exception.getMessage());
            redirectAttributes.addFlashAttribute("blogTitle", title);
            redirectAttributes.addFlashAttribute("blogCoverImage", coverImage == null ? "" : coverImage);
            redirectAttributes.addFlashAttribute("blogContent", content);
            return "redirect:/learner/blog/write";
        }
    }

    @GetMapping("/learner/blog/{slug}")
    public String blogDetail(@PathVariable String slug,
                             Authentication authentication,
                             Model model) {
        populateCommonModel(authentication, model);
        LearnerBlog blog = learnerBlogService.getBySlug(slug);

        model.addAttribute("blog", blog);
        model.addAttribute("recentBlogs", learnerBlogService.latestBlogs(4).stream()
                .filter(item -> !item.getSlug().equals(blog.getSlug()))
                .limit(3)
                .toList());

        return "pages/learner-blog-detail";
    }

    private void populateCommonModel(Authentication authentication, Model model) {
        boolean authenticated = isAuthenticated(authentication);
        model.addAttribute("authenticated", authenticated);
        model.addAttribute("learner", authenticated ? authentication.getName() : "guest");
        model.addAttribute("isAdmin", authenticated && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
