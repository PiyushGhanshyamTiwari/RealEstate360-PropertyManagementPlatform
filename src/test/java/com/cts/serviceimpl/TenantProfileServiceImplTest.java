package com.cts.serviceimpl;

import com.cts.dto.TenantProfileInputDTO;
import com.cts.dto.TenantProfileOutputDTO;
import com.cts.entity.TenantProfile;
import com.cts.entity.User;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.TenantProfileMapper;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UserRepository;
import com.cts.serviceimpl.TenantProfileServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TenantProfileServiceImplTest {

    @Mock
    private TenantProfileRepository tenantProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private TenantProfileServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Positive: addTenant succeeds
    @Test
    void testAddTenant_Success() throws IOException {
        TenantProfileInputDTO input = new TenantProfileInputDTO();
        input.setUserId(1);
        input.setDocumentType("PAN");
        input.setDocumentFileRef(multipartFile);

        User user = new User();
        user.setUserId(1);

        TenantProfile tenantProfile = new TenantProfile();
        TenantProfileOutputDTO outputDto = new TenantProfileOutputDTO();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(multipartFile.getOriginalFilename()).thenReturn("doc.pdf");
        when(multipartFile.getBytes()).thenReturn(new byte[0]);
        doNothing().when(multipartFile).transferTo(any(java.nio.file.Path.class));

        // Mock static mapper
        try (var mocked = mockStatic(TenantProfileMapper.class)) {
            mocked.when(() -> TenantProfileMapper.convertToTenantProfile(input, user, "1_PAN.pdf"))
                    .thenReturn(tenantProfile);
            mocked.when(() -> TenantProfileMapper.convertToTenantProfileOutputDto(tenantProfile))
                    .thenReturn(outputDto);

            when(tenantProfileRepository.save(tenantProfile)).thenReturn(tenantProfile);

            TenantProfileOutputDTO result = service.addTenant(input);

            assertNotNull(result);
            verify(tenantProfileRepository, times(1)).save(tenantProfile);
        }
    }

    // Negative: user not found
    @Test
    void testAddTenant_UserNotFound() {
        TenantProfileInputDTO input = new TenantProfileInputDTO();
        input.setUserId(99);
        input.setDocumentFileRef(multipartFile);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.addTenant(input));
    }

    // Negative: file upload failure
    @Test
    void testAddTenant_FileUploadFailure() throws IOException {
        TenantProfileInputDTO input = new TenantProfileInputDTO();
        input.setUserId(1);
        input.setDocumentType("PAN");
        input.setDocumentFileRef(multipartFile);

        User user = new User();
        user.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(multipartFile.getOriginalFilename()).thenReturn("doc.pdf");
        when(multipartFile.getBytes()).thenThrow(new IOException("IO error"));

        assertThrows(RuntimeException.class,
                () -> service.addTenant(input));
    }

    // Positive: getAllTenants
    @Test
    void testGetAllTenants() {
        TenantProfile tenant = new TenantProfile();
        TenantProfileOutputDTO dto = new TenantProfileOutputDTO();

        when(tenantProfileRepository.findAll()).thenReturn(Arrays.asList(tenant));

        try (var mocked = mockStatic(TenantProfileMapper.class)) {
            mocked.when(() -> TenantProfileMapper.convertToTenantProfileOutputDto(tenant))
                    .thenReturn(dto);

            List<TenantProfileOutputDTO> result = service.getAllTenants();

            assertEquals(1, result.size());
        }
    }

    // Positive: getTenantById
    @Test
    void testGetTenantById_Success() {
        TenantProfile tenant = new TenantProfile();
        TenantProfileOutputDTO dto = new TenantProfileOutputDTO();

        when(tenantProfileRepository.findById(1)).thenReturn(Optional.of(tenant));

        try (var mocked = mockStatic(TenantProfileMapper.class)) {
            mocked.when(() -> TenantProfileMapper.convertToTenantProfileOutputDto(tenant))
                    .thenReturn(dto);

            TenantProfileOutputDTO result = service.getTenantById(1);

            assertNotNull(result);
        }
    }

    // Negative: getTenantById not found
    @Test
    void testGetTenantById_NotFound() {
        when(tenantProfileRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(TenantIdNotFoundException.class,
                () -> service.getTenantById(99));
    }

    // Positive: getTenantByUserId
    @Test
    void testGetTenantByUserId_Success() {
        User user = new User();
        user.setUserId(1);

        TenantProfile tenant = new TenantProfile();
        TenantProfileOutputDTO dto = new TenantProfileOutputDTO();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tenantProfileRepository.findByUser(user)).thenReturn(Optional.of(tenant));

        try (var mocked = mockStatic(TenantProfileMapper.class)) {
            mocked.when(() -> TenantProfileMapper.convertToTenantProfileOutputDto(tenant))
                    .thenReturn(dto);

            TenantProfileOutputDTO result = service.getTenantByUserId(1);

            assertNotNull(result);
        }
    }

    // Negative: getTenantByUserId user not found
    @Test
    void testGetTenantByUserId_UserNotFound() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.getTenantByUserId(99));
    }

    // Negative: getTenantByUserId tenant not found
    @Test
    void testGetTenantByUserId_TenantNotFound() {
        User user = new User();
        user.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(tenantProfileRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.getTenantByUserId(1));
    }
}
