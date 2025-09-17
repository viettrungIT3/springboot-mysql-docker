package com.backend.backend.service;

import com.backend.backend.dto.user.UserCreateRequest;
import com.backend.backend.dto.user.UserResponse;
import com.backend.backend.dto.user.UserUpdateRequest;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.entity.User;
import com.backend.backend.entity.Role;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.UserMapper;
import com.backend.backend.repository.UserRepository;
import com.backend.backend.util.PageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        log.info("Creating new user with username: {}", request.getUsername());
        
        // Validate password strength
        if (!passwordService.isPasswordStrong(request.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
        }
        
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
        }

        User entity = userMapper.toEntity(request);
        // Hash password before saving
        entity.setPasswordHash(passwordService.hashPassword(request.getPassword()));
        
        User saved = userRepository.save(entity);
        log.info("User created successfully with ID: {}", saved.getId());
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản trị viên với ID: " + id));

        // Check username uniqueness if being updated
        if (request.getUsername() != null && !request.getUsername().equals(entity.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
            }
        }

        // Check email uniqueness if being updated
        if (request.getEmail() != null && !request.getEmail().equals(entity.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
            }
        }

        // Encrypt password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userMapper.updateEntity(entity, request); // partial update
        User saved = userRepository.save(entity);
        return userMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản trị viên với ID: " + id));
        return userMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("id").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<User> result = userRepository.findAll(pageable);

        return PageMapper.toPageResponse(result, userMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản trị viên với ID: " + id));
        entity.delete();
        userRepository.save(entity);
    }

    // Business Logic Methods for Identity Context
    
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    @Transactional(readOnly = true)
    public boolean authenticateUser(String username, String password) {
        log.debug("Authenticating user: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("User not found: {}", username);
            return false;
        }
        
        User user = userOpt.get();
        if (!user.isActive()) {
            log.warn("User account is inactive: {}", username);
            return false;
        }
        
        boolean isValid = passwordService.verifyPassword(password, user.getPassword());
        log.debug("Authentication result for {}: {}", username, isValid);
        return isValid;
    }
    
    @Transactional
    public UserResponse changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("Changing password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Verify current password
        if (!passwordService.verifyPassword(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        
        // Validate new password strength
        if (!passwordService.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
        }
        
        // Update password
        user.setPassword(passwordService.hashPassword(newPassword));
        User saved = userRepository.save(user);
        
        log.info("Password changed successfully for user ID: {}", userId);
        return userMapper.toResponse(saved);
    }
    
    @Transactional
    public UserResponse resetPassword(Long userId) {
        log.info("Resetting password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        // Generate new secure password
        String newPassword = passwordService.generateSecurePassword();
        user.setPassword(passwordService.hashPassword(newPassword));
        
        User saved = userRepository.save(user);
        
        log.info("Password reset successfully for user ID: {}", userId);
        return userMapper.toResponse(saved);
    }
    
    @Transactional
    public UserResponse activateUser(Long userId) {
        log.info("Activating user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        user.activate();
        User saved = userRepository.save(user);
        
        log.info("User activated successfully: {}", userId);
        return userMapper.toResponse(saved);
    }
    
    @Transactional
    public UserResponse deactivateUser(Long userId) {
        log.info("Deactivating user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        user.deactivate();
        User saved = userRepository.save(user);
        
        log.info("User deactivated successfully: {}", userId);
        return userMapper.toResponse(saved);
    }
    
    @Transactional
    public UserResponse changeRole(Long userId, Role newRole) {
        log.info("Changing role for user ID: {} to {}", userId, newRole);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        
        user.setRole(newRole);
        User saved = userRepository.save(user);
        
        log.info("Role changed successfully for user ID: {} to {}", userId, newRole);
        return userMapper.toResponse(saved);
    }
    
    @Transactional(readOnly = true)
    public List<UserResponse> findByRole(Role role) {
        log.debug("Finding users by role: {}", role);
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        return user.hasRole(role);
    }
}
