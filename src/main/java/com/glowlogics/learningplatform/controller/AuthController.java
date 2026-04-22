package com.glowlogics.learningplatform.controller;

import com.glowlogics.learningplatform.service.AuthService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("authenticated", false);
        model.addAttribute("learner", "guest");
        model.addAttribute("isAdmin", false);
        return "pages/login";
    }

    @GetMapping("/signup")
    public String signupPage(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/dashboard";
        }
        model.addAttribute("authenticated", false);
        model.addAttribute("learner", "guest");
        model.addAttribute("isAdmin", false);
        return "pages/signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("signupError", "Passwords do not match.");
            redirectAttributes.addFlashAttribute("signupUsername", username);
            return "redirect:/signup";
        }

        try {
            authService.registerUser(username, password);
            redirectAttributes.addFlashAttribute("signupSuccess", "Account created successfully. Please log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("signupError", ex.getMessage());
            redirectAttributes.addFlashAttribute("signupUsername", username);
            return "redirect:/signup";
        }
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
