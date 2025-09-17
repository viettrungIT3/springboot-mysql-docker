package com.backend.backend.entity;

import com.backend.backend.entity.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "full_name", columnDefinition = "TEXT")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    // Password hashing methods
    public void setPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.passwordHash = encoder.encode(rawPassword);
    }

    public String getPassword() {
        return this.passwordHash;
    }

    public boolean checkPassword(String rawPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, this.passwordHash);
    }

    // Additional methods for DDD compliance
    public boolean isActive() {
        return !isDeleted();
    }

    public void activate() {
        // User is active by default, this method is for future use
        restore();
    }

    public void deactivate() {
        // Soft delete - set deleted_at timestamp
        delete();
    }

    public boolean hasRole(Role requiredRole) {
        return this.role == requiredRole;
    }

    public boolean hasAnyRole(Role... roles) {
        for (Role role : roles) {
            if (this.role == role) {
                return true;
            }
        }
        return false;
    }

    public String getRoleName() {
        return role.name();
    }

    public String getUsernameValue() {
        return username;
    }

    public String getEmailValue() {
        return email;
    }

    public String getFullNameValue() {
        return fullName;
    }

    // Additional getters for compatibility
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }
}

