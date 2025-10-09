package com.backend.backend.controller;

import com.backend.backend.security.JwtUtil;
import com.backend.backend.entity.User;
import com.backend.backend.service.JwtTokenService;
import com.backend.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Đăng nhập và nhận JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
            @ApiResponse(responseCode = "401", description = "Sai username hoặc password")
    })
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Generate token with domain user info (includes userId) so downstream profile can resolve user
        Optional<User> userOpt = userService.findByUsername(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Không tìm thấy người dùng"));
        }
        String token = jwtTokenService.generateToken(userOpt.get());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", userDetails.getUsername(),
                "authorities", userDetails.getAuthorities()
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Đăng ký tài khoản mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ hoặc tài khoản đã tồn tại")
    })
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User registration attempt for username: {}", request.getUsername());
        
        // Create user using UserService
        var userCreateRequest = new com.backend.backend.dto.user.UserCreateRequest();
        userCreateRequest.setUsername(request.getUsername());
        userCreateRequest.setEmail(request.getEmail());
        userCreateRequest.setPassword(request.getPassword());
        userCreateRequest.setFullName(request.getFullName());
        userCreateRequest.setRole("USER");
        
        var userResponse = userService.create(userCreateRequest);
        
        // Generate token for the new user
        Optional<User> userOpt = userService.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Failed to create user");
        }
        
        String token = jwtTokenService.generateToken(userOpt.get());
        
        return ResponseEntity.ok(Map.of(
                "message", "Đăng ký thành công",
                "token", token,
                "user", userResponse
        ));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Làm mới JWT token")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token được làm mới thành công"),
            @ApiResponse(responseCode = "401", description = "Token không hợp lệ")
    })
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        if (!jwtTokenService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Token không hợp lệ"));
        }
        
        Optional<User> userOpt = jwtTokenService.getUserFromToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Không tìm thấy người dùng"));
        }
        
        String newToken = jwtTokenService.generateToken(userOpt.get());
        
        return ResponseEntity.ok(Map.of(
                "token", newToken,
                "username", userOpt.get().getUsername(),
                "role", userOpt.get().getRole().name()
        ));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Đổi mật khẩu")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "Mật khẩu hiện tại không đúng hoặc mật khẩu mới không hợp lệ"),
            @ApiResponse(responseCode = "401", description = "Token không hợp lệ")
    })
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangePasswordRequest request) {
        
        String token = authHeader.substring(7);
        
        if (!jwtTokenService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Token không hợp lệ"));
        }
        
        Optional<User> userOpt = jwtTokenService.getUserFromToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Không tìm thấy người dùng"));
        }
        
        try {
            var userResponse = userService.changePassword(
                    userOpt.get().getId(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );
            
            return ResponseEntity.ok(Map.of(
                    "message", "Đổi mật khẩu thành công",
                    "user", userResponse
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Lấy thông tin profile của người dùng hiện tại")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy profile thành công"),
            @ApiResponse(responseCode = "401", description = "Token không hợp lệ")
    })
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        
        if (!jwtTokenService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Token không hợp lệ"));
        }
        
        Optional<User> userOpt = jwtTokenService.getUserFromToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Không tìm thấy người dùng"));
        }
        
        User user = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole().name(),
                "isActive", user.isActive(),
                "createdAt", user.getCreatedAt(),
                "updatedAt", user.getUpdatedAt()
        ));
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String fullName;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
