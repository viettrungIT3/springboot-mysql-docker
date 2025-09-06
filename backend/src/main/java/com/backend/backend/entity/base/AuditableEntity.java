package com.backend.backend.entity.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Base entity class providing audit fields (created_at, updated_at, deleted_at)
 * for all entities in the system.
 * 
 * Features:
 * - Automatic timestamp management via Hibernate annotations
 * - Soft delete support with deleted_at field
 * - Non-updatable created_at field
 * - Auto-updated updated_at field
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AuditableEntity {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Check if this entity is soft deleted
     * @return true if deleted_at is not null
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * Mark this entity as soft deleted
     */
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Restore this entity from soft delete
     */
    public void restore() {
        this.deletedAt = null;
    }
}
