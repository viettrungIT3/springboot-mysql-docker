package com.backend.backend.shared.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base entity providing common audit fields and soft delete functionality.
 * This is the foundation for all domain entities in the system.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    /**
     * Soft delete the entity by setting the deleted_at timestamp.
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
    
    /**
     * Restore a soft-deleted entity by clearing the deleted_at timestamp.
     */
    public void restore() {
        this.deletedAt = null;
    }
    
    /**
     * Check if the entity is deleted.
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    /**
     * Check if the entity is active (not deleted).
     */
    public boolean isActive() {
        return !isDeleted();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BaseEntity that = (BaseEntity) obj;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
