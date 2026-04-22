package com.glowlogics.learningplatform.controller;

import com.glowlogics.learningplatform.service.CourseService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final CourseService courseService;

    public AdminController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/admin/courses")
    public String adminCourses(Authentication authentication, Model model) {
        populateCommonModel(authentication, model);
        model.addAttribute("courses", courseService.findAll("", ""));
        return "pages/admin-courses";
    }

    @PostMapping("/admin/courses")
    public String createCourse(@RequestParam String title,
                               @RequestParam String description,
                               @RequestParam String category,
                               @RequestParam String duration,
                               @RequestParam String level,
                               @RequestParam(defaultValue = "false") boolean featured,
                               RedirectAttributes redirectAttributes) {
        courseService.createCourse(title, description, category, duration, level, featured);
        redirectAttributes.addFlashAttribute("adminSuccess", "Course created successfully.");
        return "redirect:/admin/courses";
    }

    @PostMapping("/admin/courses/{courseId}")
    public String updateCourse(@PathVariable Long courseId,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam String category,
                               @RequestParam String duration,
                               @RequestParam String level,
                               @RequestParam(defaultValue = "false") boolean featured,
                               RedirectAttributes redirectAttributes) {
        courseService.updateCourse(courseId, title, description, category, duration, level, featured);
        redirectAttributes.addFlashAttribute("adminSuccess", "Course updated successfully.");
        return "redirect:/admin/courses";
    }

    private void populateCommonModel(Authentication authentication, Model model) {
        model.addAttribute("authenticated", true);
        model.addAttribute("learner", authentication.getName());
        model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
    }
}
