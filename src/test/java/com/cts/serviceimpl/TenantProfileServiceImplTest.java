package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cts.dto.TenantProfileInputDTO;
import com.cts.dto.TenantProfileOutputDTO;
import com.cts.entity.TenantProfile;
import com.cts.entity.User;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.TenantProfileMapper;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class TenantProfileServiceImplTest {

    @Mock
    private TenantProfileRepository tenantRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private TenantProfileServiceImpl service;

    private User user;
    private TenantProfile tenant;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1);

        tenant = new TenantProfile();
        tenant.setTenantId(10);
        tenant.setUser(user);
    }

   
    @Test
    void testAddTenantSuccess() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "doc.pdf", "application/pdf", "data".getBytes()
        );

        TenantProfileInputDTO input = new TenantProfileInputDTO();
        input.setUserId(1);
        input.setDocumentType("ID");
        input.setDocumentFileRef(file);

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(tenantRepo.save(any())).thenReturn(tenant);

        try (MockedStatic<TenantProfileMapper> mapper = mockStatic(TenantProfileMapper.class)) {

            mapper.when(() -> TenantProfileMapper.convertToTenantProfile(any(), eq(user), any()))
                    .thenReturn(tenant);

            mapper.when(() -> TenantProfileMapper.convertToTenantProfileOutputDto(tenant))
                    .thenReturn(new TenantProfileOutputDTO());

            TenantProfileOutputDTO result = service.addTenant(input);

            assertNotNull(result);
            verify(tenantRepo).save(tenant);
        }
    }

    
    @Test
    void testAddTenantUserNotFound() {
        TenantProfileInputDTO input = new TenantProfileInputDTO();
        input.setUserId(1);

        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.addTenant(input));
    }

    
    @Test
    void testAddTenantIOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("doc.pdf");
        when(file.getBytes()).thenThrow(new IOException());

        TenantProfileInputDTO input = new TenantProfileInputDTO();
        input.setUserId(1);
        input.setDocumentFileRef(file);

        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class,
                () -> service.addTenant(input));
    }

   
    @Test
    void testGetAllTenants() {
        when(tenantRepo.findAll()).thenReturn(List.of(tenant));

        try (MockedStatic<TenantProfileMapper> mapper = mockStatic(TenantProfileMapper.class)) {

            mapper.when(() -> TenantProfileMapper.convertToTenantProfileOutputDto(tenant))
                    .thenReturn(new TenantProfileOutputDTO());

            List<TenantProfileOutputDTO> result = service.getAllTenants();

            assertEquals(1, result.size());
        }
    }

    
    @Test
    void testGetAllTenantsEmpty() {
        when(tenantRepo.findAll()).thenReturn(Collections.emptyList());

        List<TenantProfileOutputDTO> result = service.getAllTenants();

        assertTrue(result.isEmpty());
    }

    
    @Test
    void testGetTenantByIdSuccess() {
        when(tenantRepo.findById(1)).thenReturn(Optional.of(tenant));

        try (MockedStatic<TenantProfileMapper> mapper = mockStatic(TenantProfileMapper.class)) {

            mapper.when(() -> TenantProfileMapper.convertToTenantProfileOutputDto(tenant))
                    .thenReturn(new TenantProfileOutputDTO());

            assertNotNull(service.getTenantById(1));
        }
    }

    
    @Test
    void testGetTenantByIdNotFound() {
        when(tenantRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(TenantIdNotFoundException.class,
                () -> service.getTenantById(1));
    }

   
    @Test
    void testGetTenantByUserIdSuccess() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(tenantRepo.findByUser(user)).thenReturn(Optional.of(tenant));

        try (MockedStatic<TenantProfileMapper> mapper = mockStatic(TenantProfileMapper.class)) {

            mapper.when(() -> TenantProfileMapper.convertToTenantProfileOutputDto(tenant))
                    .thenReturn(new TenantProfileOutputDTO());

            assertNotNull(service.getTenantByUserId(1));
        }
    }

    
    @Test
    void testGetTenantByUserIdUserNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.getTenantByUserId(1));
    }

    
    @Test
    void testGetTenantByUserIdTenantNotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(tenantRepo.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.getTenantByUserId(1));
    }
}