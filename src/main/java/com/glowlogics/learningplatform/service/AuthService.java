package com.glowlogics.learningplatform.service;

import com.glowlogics.learningplatform.model.UserAccount;
import com.glowlogics.learningplatform.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AuthService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserAccount registerUser(String username, String rawPassword) {
        String normalizedUsername = normalize(username);
        if (normalizedUsername.length() < 3) {
            throw new IllegalArgumentException("Username must have at least 3 characters.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must have at least 6 characters.");
        }
        if (userAccountRepository.existsByUsername(normalizedUsername)) {
            throw new IllegalArgumentException("Username is already in use.");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        return userAccountRepository.save(normalizedUsername, passwordHash, "ROLE_USER");
    }

    public void ensureUser(String username, String rawPassword, String role) {
        String normalizedUsername = normalize(username);
        if (!userAccountRepository.existsByUsername(normalizedUsername)) {
            userAccountRepository.save(normalizedUsername, passwordEncoder.encode(rawPassword), role);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(
                account.getUsername(),
                account.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(account.getRole()))
        );
    }

    private String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }
}
