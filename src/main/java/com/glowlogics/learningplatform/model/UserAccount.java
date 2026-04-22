package com.glowlogics.learningplatform.model;

public class UserAccount {
    private final Long id;
    private final String username;
    private final String passwordHash;
    private final String role;

    public UserAccount(Long id, String username, String passwordHash, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }
}
