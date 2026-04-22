package com.glowlogics.learningplatform;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LearningPlatformApplication {
    private static final int DEFAULT_PORT = 8080;
    private static final int MAX_PORT_CANDIDATE = 8100;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(LearningPlatformApplication.class);

        if (!hasExplicitPort(args)) {
            int selectedPort = findAvailablePort(DEFAULT_PORT, MAX_PORT_CANDIDATE);
            if (selectedPort != DEFAULT_PORT) {
                System.setProperty("server.port", String.valueOf(selectedPort));
                System.out.printf(
                    "Port %d is busy, starting Learning Platform on http://localhost:%d instead.%n",
                    DEFAULT_PORT,
                    selectedPort
                );
            }
        }

        application.run(args);
    }

    private static boolean hasExplicitPort(String[] args) {
        return System.getProperty("server.port") != null
            || System.getProperty("PORT") != null
            || System.getenv("SERVER_PORT") != null
            || System.getenv("PORT") != null
            || Arrays.stream(args).anyMatch(arg ->
                arg.startsWith("--server.port=")
                    || arg.startsWith("--SERVER_PORT=")
                    || arg.startsWith("--PORT=")
            );
    }

    private static int findAvailablePort(int start, int endInclusive) {
        for (int port = start; port <= endInclusive; port++) {
            if (isPortAvailable(port)) {
                return port;
            }
        }
        throw new IllegalStateException(
            "No available local port found between " + start + " and " + endInclusive + "."
        );
    }

    private static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }
}
