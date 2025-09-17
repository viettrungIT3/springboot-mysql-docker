package com.backend.backend.entity.base;

import com.backend.backend.shared.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Legacy AuditableEntity class for backward compatibility.
 * This class extends the new BaseEntity from shared domain.
 * 
 * @deprecated Use BaseEntity from shared.domain.entity package instead
 */
@Deprecated
@Getter
@Setter
public abstract class AuditableEntity extends BaseEntity {
    
    // This class is kept for backward compatibility
    // All functionality is now provided by BaseEntity
}
