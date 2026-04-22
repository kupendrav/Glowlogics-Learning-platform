package com.glowlogics.learningplatform.config;

import com.glowlogics.learningplatform.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserSeedConfig {

    @Bean
    CommandLineRunner seedUsers(AuthService authService) {
        return args -> {
            authService.ensureUser("admin", "Admin@123", "ROLE_ADMIN");
            authService.ensureUser("learner", "Learner@123", "ROLE_USER");
        };
    }
}
