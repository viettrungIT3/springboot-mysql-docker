package com.backend.backend.service;

import com.backend.backend.dto.SupplierDTO;
import com.backend.backend.entity.Supplier;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<SupplierDTO> findAll() {
        return supplierRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SupplierDTO findById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + id));
        return convertToDTO(supplier);
    }

    public SupplierDTO create(SupplierDTO dto) {
        Supplier supplier = convertToEntity(dto);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return convertToDTO(savedSupplier);
    }

    public SupplierDTO update(Long id, SupplierDTO dto) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + id));

        existing.setName(dto.getName());
        existing.setContactInfo(dto.getContactInfo());

        Supplier savedSupplier = supplierRepository.save(existing);
        return convertToDTO(savedSupplier);
    }

    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy nhà cung cấp với ID: " + id);
        }
        supplierRepository.deleteById(id);
    }

    private SupplierDTO convertToDTO(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setContactInfo(supplier.getContactInfo());
        return dto;
    }

    private Supplier convertToEntity(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setName(dto.getName());
        supplier.setContactInfo(dto.getContactInfo());
        return supplier;
    }
}
