package com.backend.backend.service;

import com.backend.backend.dto.administrator.AdministratorCreateRequest;
import com.backend.backend.dto.administrator.AdministratorResponse;
import com.backend.backend.dto.administrator.AdministratorUpdateRequest;
import com.backend.backend.dto.common.PageResponse;
import com.backend.backend.entity.Administrator;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.AdministratorMapper;
import com.backend.backend.repository.AdministratorRepository;
import com.backend.backend.util.PageMapper;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final AdministratorMapper administratorMapper;
    private final PasswordEncoder passwordEncoder;

    public AdministratorService(AdministratorRepository administratorRepository, 
                               AdministratorMapper administratorMapper,
                               PasswordEncoder passwordEncoder) {
        this.administratorRepository = administratorRepository;
        this.administratorMapper = administratorMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AdministratorResponse create(AdministratorCreateRequest request) {
        // Check if username or email already exists
        if (administratorRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
        }
        if (administratorRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
        }

        Administrator entity = administratorMapper.toEntity(request);
        // Encrypt password before saving
        entity.setPassword(request.getPassword());
        
        Administrator saved = administratorRepository.save(entity);
        return administratorMapper.toResponse(saved);
    }

    @Transactional
    public AdministratorResponse update(Long id, AdministratorUpdateRequest request) {
        Administrator entity = administratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản trị viên với ID: " + id));

        // Check username uniqueness if being updated
        if (request.getUsername() != null && !request.getUsername().equals(entity.getUsername())) {
            if (administratorRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + request.getUsername());
            }
        }

        // Check email uniqueness if being updated
        if (request.getEmail() != null && !request.getEmail().equals(entity.getEmail())) {
            if (administratorRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email đã tồn tại: " + request.getEmail());
            }
        }

        // Encrypt password if provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            request.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        administratorMapper.updateEntity(entity, request); // partial update
        Administrator saved = administratorRepository.save(entity);
        return administratorMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AdministratorResponse getById(Long id) {
        Administrator entity = administratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản trị viên với ID: " + id));
        return administratorMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<AdministratorResponse> findAll() {
        return administratorRepository.findAll().stream()
                .map(administratorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<AdministratorResponse> list(int page, int size, String sort) {
        Sort s = (sort == null || sort.isBlank()) ? Sort.by("id").descending() : Sort.by(sort);
        Pageable pageable = PageRequest.of(page, size, s);
        Page<Administrator> result = administratorRepository.findAll(pageable);

        return PageMapper.toPageResponse(result, administratorMapper::toResponse);
    }

    @Transactional
    public void delete(Long id) {
        Administrator entity = administratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản trị viên với ID: " + id));
        entity.markAsDeleted();
        administratorRepository.save(entity);
    }
}
