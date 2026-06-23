package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cts.dto.ApplicationInputDTO;
import com.cts.dto.ApplicationOutputDTO;
import com.cts.entity.Application;
import com.cts.entity.Lease;
import com.cts.entity.Property;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.entity.User;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.ApplicationMapper;
import com.cts.mapper.LeaseMapper;
import com.cts.repository.ApplicationRepository;
import com.cts.repository.LeaseRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
import com.cts.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TenantProfileRepository tenantProfileRepository;

    @Mock
    private LeaseRepository leaseRepository;

    @InjectMocks
    private ApplicationServiceImpl service;

    private Unit unit;
    private User user;
    private Application application;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUserId(1);
        user.setEmailId("owner@test.com");

        User owner = new User();
        owner.setUserId(1);

        Property property = new Property();
        property.setUser(owner);

        unit = new Unit();
        unit.setUnitId(100);
        unit.setProperty(property);

        application = new Application();
        application.setApplicationId(10);
        application.setUser(user);
        application.setUnit(unit);
        application.setStatus(Application.Status.Pending);
    }

    @Test
    void submitApplication_Success() {

        ApplicationInputDTO input = new ApplicationInputDTO();
        input.setUnitId(100);
        input.setUserId(1);

        ApplicationOutputDTO output = new ApplicationOutputDTO();

        when(unitRepository.findById(100))
                .thenReturn(Optional.of(unit));

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        when(applicationRepository.save(any(Application.class)))
                .thenReturn(application);

        try (MockedStatic<ApplicationMapper> mapper =
                     mockStatic(ApplicationMapper.class)) {

            mapper.when(() ->
                    ApplicationMapper.convertToApllication(
                            any(), any(), any()))
                    .thenReturn(application);

            mapper.when(() ->
                    ApplicationMapper.convertToApplicationOutputDto(application))
                    .thenReturn(output);

            ApplicationOutputDTO result =
                    service.submitApplication(input);

            assertNotNull(result);
        }
    }

    @Test
    void submitApplication_UnitNotFound() {

        ApplicationInputDTO input = new ApplicationInputDTO();
        input.setUnitId(100);

        when(unitRepository.findById(100))
                .thenReturn(Optional.empty());

        assertThrows(UnitIdNotFoundException.class,
                () -> service.submitApplication(input));
    }

    @Test
    void submitApplication_UserNotFound() {

        ApplicationInputDTO input = new ApplicationInputDTO();
        input.setUnitId(100);
        input.setUserId(1);

        when(unitRepository.findById(100))
                .thenReturn(Optional.of(unit));

        when(userRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.submitApplication(input));
    }

    @Test
    void getApplicationsByUnitId_Success() {

        ApplicationOutputDTO dto = new ApplicationOutputDTO();

        when(applicationRepository.getApplicationsByUnitId(100))
                .thenReturn(List.of(application));

        try (MockedStatic<ApplicationMapper> mapper =
                     mockStatic(ApplicationMapper.class)) {

            mapper.when(() ->
                    ApplicationMapper.convertToApplicationOutputDto(application))
                    .thenReturn(dto);

            List<ApplicationOutputDTO> result =
                    service.getApplicationsByUnitId(100);

            assertEquals(1, result.size());
        }
    }

    @Test
    void updateStatus_Approved_CreateLease() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        TenantProfile tenant = new TenantProfile();
        tenant.setTenantId(1);

        Lease lease = new Lease();

        ApplicationOutputDTO dto = new ApplicationOutputDTO();

        when(applicationRepository.findById(10))
                .thenReturn(Optional.of(application));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(user);

        when(applicationRepository.save(any()))
                .thenReturn(application);

        when(leaseRepository
                .existsByUnit_UnitIdAndTenantProfile_TenantId(
                        100, 1))
                .thenReturn(false);

        when(tenantProfileRepository
                .findByUser_UserId(1))
                .thenReturn(Optional.of(tenant));

        try (MockedStatic<ApplicationMapper> mapper =
                     mockStatic(ApplicationMapper.class);
             MockedStatic<LeaseMapper> leaseMapper =
                     mockStatic(LeaseMapper.class)) {

            leaseMapper.when(() ->
                    LeaseMapper.convertFromApplication(
                            application, tenant))
                    .thenReturn(lease);

            mapper.when(() ->
                    ApplicationMapper.convertToApplicationOutputDto(application))
                    .thenReturn(dto);

            ApplicationOutputDTO result =
                    service.updateStatusOfApplication(
                            10, "Approved");

            assertNotNull(result);

            verify(leaseRepository).save(any(Lease.class));
        }
    }

    @Test
    void updateStatus_Approved_LeaseAlreadyExists() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        ApplicationOutputDTO dto = new ApplicationOutputDTO();

        when(applicationRepository.findById(10))
                .thenReturn(Optional.of(application));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(user);

        when(applicationRepository.save(any()))
                .thenReturn(application);

        when(leaseRepository
                .existsByUnit_UnitIdAndTenantProfile_TenantId(
                        100, 1))
                .thenReturn(true);

        try (MockedStatic<ApplicationMapper> mapper =
                     mockStatic(ApplicationMapper.class)) {

            mapper.when(() ->
                    ApplicationMapper.convertToApplicationOutputDto(application))
                    .thenReturn(dto);

            service.updateStatusOfApplication(
                    10, "Approved");

            verify(leaseRepository, never())
                    .save(any());
        }
    }

    @Test
    void updateStatus_Rejected() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        ApplicationOutputDTO dto = new ApplicationOutputDTO();

        when(applicationRepository.findById(10))
                .thenReturn(Optional.of(application));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(user);

        when(applicationRepository.save(any()))
                .thenReturn(application);

        try (MockedStatic<ApplicationMapper> mapper =
                     mockStatic(ApplicationMapper.class)) {

            mapper.when(() ->
                    ApplicationMapper.convertToApplicationOutputDto(application))
                    .thenReturn(dto);

            assertNotNull(
                    service.updateStatusOfApplication(
                            10, "Rejected"));
        }
    }

    @Test
    void updateStatus_ApplicationNotFound() {

        when(applicationRepository.findById(10))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.updateStatusOfApplication(
                        10, "Approved"));
    }

    @Test
    void updateStatus_UserNotFound() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        when(applicationRepository.findById(10))
                .thenReturn(Optional.of(application));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(null);

        assertThrows(UserIdNotFoundException.class,
                () -> service.updateStatusOfApplication(
                        10, "Approved"));
    }

    @Test
    void updateStatus_AccessDenied() {

        User anotherUser = new User();
        anotherUser.setUserId(99);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        when(applicationRepository.findById(10))
                .thenReturn(Optional.of(application));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(anotherUser);

        assertThrows(AccessDeniedException.class,
                () -> service.updateStatusOfApplication(
                        10, "Approved"));
    }

    @Test
    void updateStatus_TenantProfileNotFound() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com", null));

        when(applicationRepository.findById(10))
                .thenReturn(Optional.of(application));

        when(userRepository.findUserByEmail("owner@test.com"))
                .thenReturn(user);

        when(applicationRepository.save(any()))
                .thenReturn(application);

        when(leaseRepository
                .existsByUnit_UnitIdAndTenantProfile_TenantId(
                        100, 1))
                .thenReturn(false);

        when(tenantProfileRepository
                .findByUser_UserId(1))
                .thenReturn(Optional.empty());

        assertThrows(TenantIdNotFoundException.class,
                () -> service.updateStatusOfApplication(
                        10, "Approved"));
    }

    @Test
    void getApplicationByTenantId_Success() {

        ApplicationOutputDTO dto = new ApplicationOutputDTO();

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        when(applicationRepository
                .getApplicationByTenantId(1))
                .thenReturn(Arrays.asList(application));

        try (MockedStatic<ApplicationMapper> mapper =
                     mockStatic(ApplicationMapper.class)) {

            mapper.when(() ->
                    ApplicationMapper.convertToApplicationOutputDto(application))
                    .thenReturn(dto);

            List<ApplicationOutputDTO> result =
                    service.getApplicationByTenantId(1);

            assertEquals(1, result.size());
        }
    }

    @Test
    void getApplicationByTenantId_UserNotFound() {

        when(userRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> service.getApplicationByTenantId(1));
    }

    @Test
    void getApplicationByApplicationId_Success() {

        ApplicationOutputDTO dto =
                new ApplicationOutputDTO();

        when(applicationRepository.findById(10))
                .thenReturn(Optional.of(application));

        try (MockedStatic<ApplicationMapper> mapper =
                     mockStatic(ApplicationMapper.class)) {

            mapper.when(() ->
                    ApplicationMapper.convertToApplicationOutputDto(application))
                    .thenReturn(dto);

            ApplicationOutputDTO result =
                    service.getApplicationByApplicationId(10);

            assertNotNull(result);
        }
    }

    @Test
    void getApplicationByApplicationId_NotFound() {

        when(applicationRepository.findById(10))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getApplicationByApplicationId(10));
    }
}
