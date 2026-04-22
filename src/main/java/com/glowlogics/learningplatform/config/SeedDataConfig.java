package com.glowlogics.learningplatform.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.glowlogics.learningplatform.model.Course;
import com.glowlogics.learningplatform.model.Lesson;
import com.glowlogics.learningplatform.model.LessonResource;
import com.glowlogics.learningplatform.repository.CourseRepository;

@Configuration
public class SeedDataConfig {

    private static final String DISCORD_URL = "https://discord.gg/uuqdBbgc";
    private static final String DEFAULT_COURSE_IMAGE = "https://images.pexels.com/photos/177598/pexels-photo-177598.jpeg?auto=compress&cs=tinysrgb&w=1200";
    private static final Map<String, String> COURSE_IMAGE_BY_SLUG = Map.ofEntries(
            Map.entry("full-stack-java-development", "https://images.pexels.com/photos/177598/pexels-photo-177598.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("data-science", "https://images.pexels.com/photos/10468213/pexels-photo-10468213.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("web-development", "https://images.pexels.com/photos/14553720/pexels-photo-14553720.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("cyber-security-ethical-hacking", "https://images.pexels.com/photos/5483248/pexels-photo-5483248.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("app-development", "https://images.pexels.com/photos/48606/pexels-photo-48606.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("dsa-in-java", "https://images.pexels.com/photos/27427258/pexels-photo-27427258.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("cloud-computing", "https://images.pexels.com/photos/5475760/pexels-photo-5475760.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("internet-of-things", "https://images.pexels.com/photos/35652454/pexels-photo-35652454.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("power-bi", "https://images.pexels.com/photos/5466250/pexels-photo-5466250.jpeg?auto=compress&cs=tinysrgb&w=1200"),
            Map.entry("vlsi", "https://images.pexels.com/photos/6755081/pexels-photo-6755081.jpeg?auto=compress&cs=tinysrgb&w=1200")
    );

    @Bean
    CommandLineRunner seedCourses(CourseRepository courseRepository) {
        return args -> courseRepository.saveAll(buildCourses());
    }

    private List<Course> buildCourses() {
        return List.of(
                course(
                        1L,
                        "full-stack-java-development",
                        "Full-stack Java Development",
                        "Learn Java backend, REST APIs, databases, and frontend integration using a project-first roadmap.",
                        "Software Engineering",
                        "24 weeks",
                        "Beginner to Advanced",
                        true,
                        List.of(
                                "Core Java and OOP foundations",
                                "Spring Boot APIs and data layer",
                                "Frontend integration with modern UI",
                                "Deployment, monitoring, and interview prep"
                        ),
                        List.of(
                                lesson(101L, "Java + Spring Boot Project Setup", "https://www.youtube.com/embed/9SGDpanrc8U",
                                        "Build your first full-stack Java workflow, project structure, and local environment.",
                                        List.of(
                                                resource("Spring Boot Docs", "https://spring.io/projects/spring-boot"),
                                                resource("Java Roadmap", "https://roadmap.sh/java")
                                        )),
                                lesson(102L, "REST APIs and Database Integration", "https://www.youtube.com/embed/zvR-Oif_nxg",
                                        "Design real CRUD APIs and connect them to relational data stores.",
                                        List.of(
                                                resource("Spring Data JPA", "https://spring.io/projects/spring-data-jpa"),
                                                resource("PostgreSQL Tutorial", "https://www.postgresql.org/docs/")
                                        )),
                                lesson(103L, "Frontend and Deployment Workflow", "https://www.youtube.com/embed/XsA7P2qM0gA",
                                        "Connect backend + frontend and deploy production-ready full-stack projects.",
                                        List.of(
                                                resource("Thymeleaf Guide", "https://www.thymeleaf.org/documentation.html"),
                                                resource("Docker Getting Started", "https://docs.docker.com/get-started/")
                                        ))
                        )
                ),
                course(
                        2L,
                        "data-science",
                        "Data Science",
                        "Master data analysis, statistics, machine learning, and practical model deployment.",
                        "Data",
                        "20 weeks",
                        "Beginner to Intermediate",
                        true,
                        List.of(
                                "Python, NumPy, and Pandas fundamentals",
                                "Data visualization and storytelling",
                                "Machine learning model lifecycle",
                                "Model evaluation and deployment basics"
                        ),
                        List.of(
                                lesson(201L, "Python for Data Science", "https://www.youtube.com/embed/rfscVS0vtbw",
                                        "Start with Python tooling and exploratory analysis workflows.",
                                        List.of(
                                                resource("Pandas Docs", "https://pandas.pydata.org/docs/"),
                                                resource("NumPy Docs", "https://numpy.org/doc/")
                                        )),
                                lesson(202L, "Data Visualization Essentials", "https://www.youtube.com/embed/6GUZXDef2U0",
                                        "Create understandable charts and dashboards for business decisions.",
                                        List.of(
                                                resource("Matplotlib Guide", "https://matplotlib.org/stable/users/index"),
                                                resource("Seaborn Tutorial", "https://seaborn.pydata.org/tutorial.html")
                                        )),
                                lesson(203L, "Intro to Machine Learning", "https://www.youtube.com/embed/7eh4d6sabA0",
                                        "Train and validate baseline models using clean pipelines.",
                                        List.of(
                                                resource("Scikit-learn", "https://scikit-learn.org/stable/user_guide.html"),
                                                resource("Kaggle Learn", "https://www.kaggle.com/learn")
                                        ))
                        )
                ),
                course(
                        3L,
                        "web-development",
                        "Web Development",
                        "Build responsive web applications with HTML, CSS, JavaScript, and modern frameworks.",
                        "Web",
                        "16 weeks",
                        "Beginner to Intermediate",
                        true,
                        List.of(
                                "HTML and semantic structure",
                                "CSS layout systems and responsive patterns",
                                "JavaScript interactivity and APIs",
                                "Framework fundamentals and deployment"
                        ),
                        List.of(
                                lesson(301L, "HTML and CSS Foundations", "https://www.youtube.com/embed/mU6anWqZJcc",
                                        "Learn structured markup and flexible layouts with modern CSS.",
                                        List.of(
                                                resource("MDN HTML", "https://developer.mozilla.org/en-US/docs/Web/HTML"),
                                                resource("MDN CSS", "https://developer.mozilla.org/en-US/docs/Web/CSS")
                                        )),
                                lesson(302L, "JavaScript for Web Apps", "https://www.youtube.com/embed/PkZNo7MFNFg",
                                        "Use JavaScript to build rich interactions and dynamic views.",
                                        List.of(
                                                resource("JavaScript Info", "https://javascript.info/"),
                                                resource("Web APIs", "https://developer.mozilla.org/en-US/docs/Web/API")
                                        )),
                                lesson(303L, "Modern Frontend Workflow", "https://www.youtube.com/embed/w7ejDZ8SWv8",
                                        "Organize scalable frontend code and ship responsive experiences.",
                                        List.of(
                                                resource("React Docs", "https://react.dev/learn"),
                                                resource("Vite Guide", "https://vite.dev/guide/")
                                        ))
                        )
                ),
                course(
                        4L,
                        "cyber-security-ethical-hacking",
                        "Cyber Security - Ethical Hacking",
                        "Understand threat modeling, network security, and ethical hacking practices.",
                        "Security",
                        "18 weeks",
                        "Intermediate",
                        false,
                        List.of(
                                "Security fundamentals and CIA triad",
                                "Networking and attack surface analysis",
                                "Ethical hacking tools and legal boundaries",
                                "Hardening and blue-team operations"
                        ),
                        List.of(
                                lesson(401L, "Cyber Security Fundamentals", "https://www.youtube.com/embed/inWWhr5tnEA",
                                        "Understand core security concepts and practical defense layers.",
                                        List.of(
                                                resource("OWASP Top 10", "https://owasp.org/www-project-top-ten/"),
                                                resource("NIST Framework", "https://www.nist.gov/cyberframework")
                                        )),
                                lesson(402L, "Ethical Hacking Basics", "https://www.youtube.com/embed/3Kq1MIfTWCE",
                                        "Learn reconnaissance, scanning, and vulnerability assessment basics.",
                                        List.of(
                                                resource("Kali Linux", "https://www.kali.org/docs/"),
                                                resource("TryHackMe", "https://tryhackme.com/paths")
                                        )),
                                lesson(403L, "Security Monitoring and Hardening", "https://www.youtube.com/embed/4D4xw7nrif8",
                                        "Apply practical controls to secure systems and monitor incidents.",
                                        List.of(
                                                resource("MITRE ATT&CK", "https://attack.mitre.org/"),
                                                resource("Security Blue Team", "https://securityblue.team/why-btl1/")
                                        ))
                        )
                ),
                course(
                        5L,
                        "app-development",
                        "App Development",
                        "Create Android and cross-platform mobile apps with clean architecture.",
                        "Mobile",
                        "18 weeks",
                        "Beginner to Intermediate",
                        false,
                        List.of(
                                "Programming basics and UI components",
                                "App state, navigation, and storage",
                                "API integration and testing",
                                "Publishing and app lifecycle management"
                        ),
                        List.of(
                                lesson(501L, "Android App Basics", "https://www.youtube.com/embed/fis26HvvDII",
                                        "Set up Android Studio and build your first working app.",
                                        List.of(
                                                resource("Android Developers", "https://developer.android.com/docs"),
                                                resource("Kotlin Docs", "https://kotlinlang.org/docs/home.html")
                                        )),
                                lesson(502L, "Flutter Cross-platform Apps", "https://www.youtube.com/embed/x0uinJvhNxI",
                                        "Build one codebase for Android and iOS with reusable widgets.",
                                        List.of(
                                                resource("Flutter Docs", "https://docs.flutter.dev/"),
                                                resource("Dart Docs", "https://dart.dev/guides")
                                        )),
                                lesson(503L, "App Testing and Deployment", "https://www.youtube.com/embed/rIaaH87z1-g",
                                        "Test mobile apps effectively and ship releases with confidence.",
                                        List.of(
                                                resource("Firebase Docs", "https://firebase.google.com/docs"),
                                                resource("Play Console Help", "https://support.google.com/googleplay/android-developer/")
                                        ))
                        )
                ),
                course(
                        6L,
                        "dsa-in-java",
                        "DSA in Java",
                        "Develop problem-solving mastery using core data structures and algorithm patterns.",
                        "Programming",
                        "14 weeks",
                        "Beginner to Advanced",
                        false,
                        List.of(
                                "Complexity analysis and Java collections",
                                "Arrays, strings, hashing, and recursion",
                                "Trees, graphs, and dynamic programming",
                                "Interview pattern practice and mock rounds"
                        ),
                        List.of(
                                lesson(601L, "Big O and Core Patterns", "https://www.youtube.com/embed/B31LgI4Y4DQ",
                                        "Use complexity analysis to choose scalable algorithms.",
                                        List.of(
                                                resource("NeetCode Roadmap", "https://neetcode.io/roadmap"),
                                                resource("LeetCode", "https://leetcode.com/")
                                        )),
                                lesson(602L, "Trees and Graphs in Java", "https://www.youtube.com/embed/oSWTXtMglKE",
                                        "Implement traversal and shortest-path patterns in Java.",
                                        List.of(
                                                resource("GeeksforGeeks DSA", "https://www.geeksforgeeks.org/data-structures/"),
                                                resource("Java Collections", "https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/package-summary.html")
                                        )),
                                lesson(603L, "Dynamic Programming for Interviews", "https://www.youtube.com/embed/oBt53YbR9Kk",
                                        "Break complex optimization problems into repeatable DP states.",
                                        List.of(
                                                resource("CP Algorithms", "https://cp-algorithms.com/"),
                                                resource("InterviewBit", "https://www.interviewbit.com/courses/programming/")
                                        ))
                        )
                ),
                course(
                        7L,
                        "cloud-computing",
                        "Cloud Computing",
                        "Learn cloud architecture, deployment automation, and scalable service design.",
                        "Cloud",
                        "15 weeks",
                        "Intermediate",
                        false,
                        List.of(
                                "Cloud fundamentals and service models",
                                "Compute, storage, and networking services",
                                "DevOps automation and IaC",
                                "Observability, cost, and reliability"
                        ),
                        List.of(
                                lesson(701L, "Cloud Fundamentals", "https://www.youtube.com/embed/2LaAJq1lB1Q",
                                        "Understand IaaS, PaaS, SaaS, and cloud architecture principles.",
                                        List.of(
                                                resource("AWS Cloud Practitioner", "https://aws.amazon.com/training/digital/aws-cloud-practitioner-essentials/"),
                                                resource("Azure Fundamentals", "https://learn.microsoft.com/training/paths/azure-fundamentals/")
                                        )),
                                lesson(702L, "Containers and Kubernetes", "https://www.youtube.com/embed/X48VuDVv0do",
                                        "Containerize apps and orchestrate them for production scale.",
                                        List.of(
                                                resource("Kubernetes Docs", "https://kubernetes.io/docs/home/"),
                                                resource("Docker Docs", "https://docs.docker.com/")
                                        )),
                                lesson(703L, "Cloud DevOps Automation", "https://www.youtube.com/embed/0yWAtQ6wYNM",
                                        "Automate infrastructure and releases using CI/CD best practices.",
                                        List.of(
                                                resource("Terraform Docs", "https://developer.hashicorp.com/terraform/docs"),
                                                resource("GitHub Actions", "https://docs.github.com/actions")
                                        ))
                        )
                ),
                course(
                        8L,
                        "internet-of-things",
                        "Internet of Things",
                        "Build IoT systems using sensors, microcontrollers, networking, and cloud analytics.",
                        "Embedded",
                        "16 weeks",
                        "Beginner to Intermediate",
                        false,
                        List.of(
                                "Electronics and embedded foundations",
                                "Microcontroller programming",
                                "IoT protocols and cloud integration",
                                "Monitoring dashboards and automation"
                        ),
                        List.of(
                                lesson(801L, "IoT System Architecture", "https://www.youtube.com/embed/LlhmzVL5bm8",
                                        "Understand sensor-to-cloud architecture for IoT applications.",
                                        List.of(
                                                resource("Arduino Docs", "https://docs.arduino.cc/"),
                                                resource("MQTT Essentials", "https://www.hivemq.com/mqtt-essentials/")
                                        )),
                                lesson(802L, "Microcontrollers and Sensors", "https://www.youtube.com/embed/nAq3JQw3zH8",
                                        "Read sensor data and build automation logic on microcontrollers.",
                                        List.of(
                                                resource("ESP32 Guide", "https://docs.espressif.com/projects/esp-idf/en/latest/"),
                                                resource("Raspberry Pi Docs", "https://www.raspberrypi.com/documentation/")
                                        )),
                                lesson(803L, "Cloud IoT Dashboards", "https://www.youtube.com/embed/6mBO2vqLv38",
                                        "Visualize device data and create alerting workflows.",
                                        List.of(
                                                resource("Azure IoT", "https://learn.microsoft.com/azure/iot-hub/"),
                                                resource("ThingsBoard", "https://thingsboard.io/docs/")
                                        ))
                        )
                ),
                course(
                        9L,
                        "power-bi",
                        "Power BI",
                        "Create business intelligence dashboards and reports with practical data modeling.",
                        "Analytics",
                        "10 weeks",
                        "Beginner",
                        false,
                        List.of(
                                "Power BI desktop fundamentals",
                                "Data modeling and DAX formulas",
                                "Interactive dashboards and storytelling",
                                "Publishing and refresh workflows"
                        ),
                        List.of(
                                lesson(901L, "Power BI Desktop Basics", "https://www.youtube.com/embed/AGrl-H87pRU",
                                        "Import, clean, and visualize data with essential chart types.",
                                        List.of(
                                                resource("Power BI Docs", "https://learn.microsoft.com/power-bi/"),
                                                resource("DAX Guide", "https://dax.guide/")
                                        )),
                                lesson(902L, "Data Modeling and DAX", "https://www.youtube.com/embed/0E_8g5wP2Cs",
                                        "Build robust relationships and measures for analytics.",
                                        List.of(
                                                resource("SQLBI", "https://www.sqlbi.com/"),
                                                resource("Microsoft Learn", "https://learn.microsoft.com/training/powerplatform/power-bi/")
                                        )),
                                lesson(903L, "Dashboard Publishing and Sharing", "https://www.youtube.com/embed/c7LrqSxjJQQ",
                                        "Publish dashboards and configure refresh with stakeholder access.",
                                        List.of(
                                                resource("Power BI Service", "https://learn.microsoft.com/power-bi/fundamentals/service-business-users-overview"),
                                                resource("Fabric Community", "https://community.fabric.microsoft.com/")
                                        ))
                        )
                ),
                course(
                        10L,
                        "vlsi",
                        "VLSI",
                        "Understand digital design, verification, and chip implementation flows for VLSI careers.",
                        "Hardware",
                        "22 weeks",
                        "Intermediate to Advanced",
                        false,
                        List.of(
                                "Digital logic and CMOS basics",
                                "RTL design using Verilog/SystemVerilog",
                                "Verification and timing analysis",
                                "Physical design and sign-off"
                        ),
                        List.of(
                                lesson(1001L, "Digital Design Foundations", "https://www.youtube.com/embed/sTu3LwpF6XI",
                                        "Build core logic design intuition for chip architecture.",
                                        List.of(
                                                resource("NPTEL VLSI", "https://nptel.ac.in/courses/117106093"),
                                                resource("CMOS Notes", "https://ocw.mit.edu/courses/6-004-computation-structures-spring-2017/")
                                        )),
                                lesson(1002L, "Verilog and RTL Design", "https://www.youtube.com/embed/t7AgnYk5QXY",
                                        "Model digital systems and test RTL modules effectively.",
                                        List.of(
                                                resource("Verilog Tutorial", "https://www.chipverify.com/verilog/verilog-tutorial"),
                                                resource("EDA Playground", "https://www.edaplayground.com/")
                                        )),
                                lesson(1003L, "Timing and Physical Design", "https://www.youtube.com/embed/Y0f3M5xdrYQ",
                                        "Learn timing closure and layout fundamentals in chip design.",
                                        List.of(
                                                resource("STA Basics", "https://www.vlsisystemdesign.com/static-timing-analysis/"),
                                                resource("OpenROAD", "https://openroad.readthedocs.io/en/latest/")
                                        ))
                        )
                )
        );
    }

    private Course course(Long id,
                          String slug,
                          String title,
                          String description,
                          String category,
                          String duration,
                          String level,
                          boolean featured,
                          List<String> roadmap,
                          List<Lesson> lessons) {
                String imageUrl = COURSE_IMAGE_BY_SLUG.getOrDefault(slug, DEFAULT_COURSE_IMAGE);
                return new Course(id, slug, title, description, category, duration, level, featured, imageUrl, DISCORD_URL, roadmap, lessons);
    }

    private Lesson lesson(Long id, String title, String videoUrl, String notes, List<LessonResource> resources) {
        return new Lesson(id, title, videoUrl, notes, resources);
    }

    private LessonResource resource(String title, String url) {
        return new LessonResource(title, url);
    }
}
