package com.backend.backend.service;

import com.backend.backend.dto.administrator.LoginRequest;
import com.backend.backend.dto.administrator.LoginResponse;
import com.backend.backend.entity.User;
import com.backend.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse authenticate(LoginRequest request) {
        // Find administrator by username
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return LoginResponse.error("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        // Check password
        if (!user.checkPassword(request.getPassword())) {
            return LoginResponse.error("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        // Generate simple token (Base64 encoded username:password for now)
        // TODO: Replace with JWT in future iterations
        String token = generateSimpleToken(user.getUsername());

        return LoginResponse.success(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getFullName()
        );
    }

    public boolean validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length != 2) return false;

            String username = parts[0];
            User user = userRepository.findByUsername(username).orElse(null);
            return user != null;
        } catch (Exception e) {
            return false;
        }
    }

    public User getUserFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length != 2) return null;

            String username = parts[0];
            return userRepository.findByUsername(username).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private String generateSimpleToken(String username) {
        // Simple token generation - in production, use JWT
        String tokenData = username + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }
}
