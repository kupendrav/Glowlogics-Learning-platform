package com.glowlogics.learningplatform.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "api-user", roles = "USER")
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void enrollEndpointShouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("enrolled"));
    }

    @Test
    void progressEndpointShouldReturnAggregatedProgress() throws Exception {
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 1
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 1,
                                  "lessonId": 101,
                                  "completed": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercent").value(33))
                .andExpect(jsonPath("$.completedLessons").value(1));
    }

    @Test
    void validationErrorsShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void syncEndpointShouldIgnoreInvalidLessonIds() throws Exception {
      mockMvc.perform(post("/api/enrollments")
          .contentType(MediaType.APPLICATION_JSON)
          .content("""
            {
              "courseId": 1
            }
            """))
        .andExpect(status().isCreated());

        mockMvc.perform(post("/api/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 1,
                                  "completedByLessonId": {
                                    "101": true,
                                    "999": true
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completedLessons").value(1))
                .andExpect(jsonPath("$.totalLessons").value(3))
                .andExpect(jsonPath("$.progressPercent").value(33));
    }
}
