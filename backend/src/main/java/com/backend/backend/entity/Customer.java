package com.backend.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "slug", length = 180, nullable = false, unique = true)
    private String slug;

    @Column(name = "contact_info", columnDefinition = "TEXT")
    private String contactInfo;
}

