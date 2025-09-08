package com.backend.backend.service;

import com.backend.backend.dto.administrator.LoginRequest;
import com.backend.backend.dto.administrator.LoginResponse;
import com.backend.backend.entity.Administrator;
import com.backend.backend.repository.AdministratorRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthenticationService {

    private final AdministratorRepository administratorRepository;

    public AuthenticationService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    public LoginResponse authenticate(LoginRequest request) {
        // Find administrator by username
        Administrator admin = administratorRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (admin == null) {
            return LoginResponse.error("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        // Check password
        if (!admin.checkPassword(request.getPassword())) {
            return LoginResponse.error("Tên đăng nhập hoặc mật khẩu không đúng");
        }

        // Generate simple token (Base64 encoded username:password for now)
        // TODO: Replace with JWT in future iterations
        String token = generateSimpleToken(admin.getUsername());

        return LoginResponse.success(
                token,
                admin.getUsername(),
                admin.getEmail(),
                admin.getFullName()
        );
    }

    public boolean validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length != 2) return false;

            String username = parts[0];
            Administrator admin = administratorRepository.findByUsername(username).orElse(null);
            return admin != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Administrator getAdministratorFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length != 2) return null;

            String username = parts[0];
            return administratorRepository.findByUsername(username).orElse(null);
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
