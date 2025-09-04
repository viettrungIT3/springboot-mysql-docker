package com.backend.backend.repository;

import com.backend.backend.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {
    Optional<Administrator> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

