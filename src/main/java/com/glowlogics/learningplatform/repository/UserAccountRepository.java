package com.glowlogics.learningplatform.repository;

import com.glowlogics.learningplatform.model.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserAccountRepository {

    private final AtomicLong idSequence = new AtomicLong(1);
    private final Map<String, UserAccount> usersByUsername = new ConcurrentHashMap<>();

    public UserAccount save(String username, String passwordHash, String role) {
        String normalizedUsername = normalize(username);
        UserAccount account = new UserAccount(idSequence.getAndIncrement(), normalizedUsername, passwordHash, role);
        usersByUsername.put(normalizedUsername, account);
        return account;
    }

    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(normalize(username)));
    }

    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(normalize(username));
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
