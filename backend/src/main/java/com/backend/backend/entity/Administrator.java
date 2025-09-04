package com.backend.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrators")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "full_name", columnDefinition = "TEXT")
    private String fullName;
}

