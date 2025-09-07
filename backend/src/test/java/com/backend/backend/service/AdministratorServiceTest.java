package com.backend.backend.service;

import com.backend.backend.dto.administrator.AdministratorCreateRequest;
import com.backend.backend.dto.administrator.AdministratorResponse;
import com.backend.backend.entity.Administrator;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.backend.backend.exception.ResourceNotFoundException;
import com.backend.backend.mapper.AdministratorMapper;
import com.backend.backend.repository.AdministratorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdministratorService Unit Tests")
class AdministratorServiceTest {

    @Mock private AdministratorRepository administratorRepository;
    @Mock private AdministratorMapper administratorMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdministratorService administratorService;

    private Administrator entity1;
    private AdministratorCreateRequest createRequest;
    private AdministratorResponse response1;

    @BeforeEach
    void setUp() {
        entity1 = Administrator.builder()
                .id(1L)
                .username("admin1")
                .passwordHash("hashedPassword1")
                .email("admin1@company.com")
                .fullName("Administrator One")
                .build();

        createRequest = new AdministratorCreateRequest();
        createRequest.setUsername("newadmin");
        createRequest.setPassword("plainPassword");
        createRequest.setEmail("newadmin@company.com");
        createRequest.setFullName("New Administrator");

        response1 = AdministratorResponse.builder()
                .id(1L)
                .username("admin1")
                .email("admin1@company.com")
                .fullName("Administrator One")
                .build();
    }

    @Test
    @DisplayName("Should create administrator successfully")
    void create_shouldReturnMappedResponse() {
        // arrange
        Administrator toSave = Administrator.builder()
                .username("newadmin")
                .passwordHash("plainPassword")
                .email("newadmin@company.com")
                .fullName("New Administrator")
                .build();

        Administrator saved = Administrator.builder()
                .id(99L)
                .username("newadmin")
                .passwordHash("hashedPassword")
                .email("newadmin@company.com")
                .fullName("New Administrator")
                .build();

        AdministratorResponse expectedResponse = AdministratorResponse.builder()
                .id(99L)
                .username("newadmin")
                .email("newadmin@company.com")
                .fullName("New Administrator")
                .build();

        given(passwordEncoder.encode("admin123")).willReturn("$2a$10$encodedPassword");
        given(administratorMapper.toEntity(createRequest)).willReturn(toSave);
        given(administratorRepository.save(toSave)).willReturn(saved);
        given(administratorMapper.toResponse(saved)).willReturn(expectedResponse);

        // act
        AdministratorResponse result = administratorService.create(createRequest);

        // assert
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getUsername()).isEqualTo("newadmin");
        verify(administratorMapper).toEntity(createRequest);
        verify(administratorRepository).save(toSave);
        verify(administratorMapper).toResponse(saved);
    }

    @Test
    @DisplayName("Should get administrator by ID successfully")
    void getById_shouldReturnAdministratorWhenExists() {
        // arrange
        given(administratorRepository.findById(1L)).willReturn(Optional.of(entity1));
        given(administratorMapper.toResponse(entity1)).willReturn(response1);

        // act
        AdministratorResponse result = administratorService.getById(1L);

        // assert
        assertThat(result).isEqualTo(response1);
        verify(administratorRepository).findById(1L);
        verify(administratorMapper).toResponse(entity1);
    }

    @Test
    @DisplayName("Should throw exception when administrator not found by ID")
    void getById_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(administratorRepository.findById(999L)).willReturn(Optional.empty());

        // act & assert
        assertThatThrownBy(() -> administratorService.getById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy quản trị viên với ID: 999");

        verify(administratorRepository).findById(999L);
        verifyNoInteractions(administratorMapper);
    }

    @Test
    @DisplayName("Should delete administrator successfully")
    void delete_shouldCallRepositoryDelete() {
        // arrange
        given(administratorRepository.existsById(1L)).willReturn(true);

        // act
        administratorService.delete(1L);

        // assert
        verify(administratorRepository).existsById(1L);
        verify(administratorRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent administrator")
    void delete_shouldThrowExceptionWhenNotFound() {
        // arrange
        given(administratorRepository.existsById(999L)).willReturn(false);

        // act & assert
        assertThatThrownBy(() -> administratorService.delete(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Không tìm thấy quản trị viên với ID: 999");

        verify(administratorRepository).existsById(999L);
        verify(administratorRepository, never()).deleteById(anyLong());
    }
}
