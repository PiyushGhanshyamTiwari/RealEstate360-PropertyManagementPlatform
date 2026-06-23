package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
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

import com.cts.dto.InvoiceInputDTO;
import com.cts.dto.InvoiceOutputDTO;
import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;
import com.cts.entity.Invoice;
import com.cts.entity.Lease;
import com.cts.entity.Property;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.entity.User;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.InvoiceMapper;
import com.cts.mapper.LeaseMapper;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.LeaseRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
import com.cts.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class LeaseServiceImplTest {

    @Mock
    private LeaseRepository leaseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private TenantProfileRepository tenantProfileRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private LeaseServiceImpl service;

    private Unit unit;
    private TenantProfile tenant;
    private Lease lease;
    private User owner;

    @BeforeEach
    void setup() {

        owner = new User();
        owner.setUserId(1);
        owner.setEmailId("owner@test.com");

        Property property = new Property();
        property.setUser(owner);

        unit = new Unit();
        unit.setUnitId(101);
        unit.setProperty(property);

        tenant = new TenantProfile();
        tenant.setTenantId(10);

        lease = new Lease();
        lease.setLeaseId(100);
        lease.setUnit(unit);
        lease.setTenantProfile(tenant);
        lease.setStartDate(LocalDate.of(2024, 1, 10));
        lease.setEndDate(LocalDate.of(2024, 3, 25));
    }

    @Test
    void leaseGeneration_Success() {

        LeaseInputDTO input = new LeaseInputDTO();
        input.setUnitId(101);
        input.setTenantId(10);

        LeaseOutputDTO dto = new LeaseOutputDTO();

        when(unitRepository.findById(101))
                .thenReturn(Optional.of(unit));

        when(tenantProfileRepository.findById(10))
                .thenReturn(Optional.of(tenant));

        when(leaseRepository.save(any()))
                .thenReturn(lease);

        try (MockedStatic<LeaseMapper> mapper =
                     mockStatic(LeaseMapper.class)) {

            mapper.when(() ->
                    LeaseMapper.convertToLease(
                            input,
                            unit,
                            tenant))
                    .thenReturn(lease);

            mapper.when(() ->
                    LeaseMapper.convertToLeaseOutputDto(lease))
                    .thenReturn(dto);

            LeaseOutputDTO result =
                    service.leaseGeneration(input);

            assertNotNull(result);
        }
    }

    @Test
    void leaseGeneration_UnitNotFound() {

        LeaseInputDTO input = new LeaseInputDTO();
        input.setUnitId(101);

        when(unitRepository.findById(101))
                .thenReturn(Optional.empty());

        assertThrows(UnitIdNotFoundException.class,
                () -> service.leaseGeneration(input));
    }

    @Test
    void leaseGeneration_TenantNotFound() {

        LeaseInputDTO input = new LeaseInputDTO();
        input.setUnitId(101);
        input.setTenantId(10);

        when(unitRepository.findById(101))
                .thenReturn(Optional.of(unit));

        when(tenantProfileRepository.findById(10))
                .thenReturn(Optional.empty());

        assertThrows(TenantIdNotFoundException.class,
                () -> service.leaseGeneration(input));
    }

    @Test
    void updateLeaseStatus_LeaseNotFound() {

        when(leaseRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () ->
                service.updateLeaseStatus(
                        1,
                        "Agreed"));
    }

    @Test
    void updateLeaseStatus_UserNotFound() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        when(leaseRepository.findById(100))
                .thenReturn(Optional.of(lease));

        when(userRepository.findUserByEmail(
                "owner@test.com"))
                .thenReturn(null);

        assertThrows(UserIdNotFoundException.class,
                () ->
                service.updateLeaseStatus(
                        100,
                        "Agreed"));
    }

    @Test
    void updateLeaseStatus_AccessDenied() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        User anotherUser = new User();
        anotherUser.setUserId(99);

        when(leaseRepository.findById(100))
                .thenReturn(Optional.of(lease));

        when(userRepository.findUserByEmail(
                "owner@test.com"))
                .thenReturn(anotherUser);

        assertThrows(AccessDeniedException.class,
                () ->
                service.updateLeaseStatus(
                        100,
                        "Agreed"));
    }

    @Test
    void updateLeaseStatus_NonAgreedStatus() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        LeaseOutputDTO dto = new LeaseOutputDTO();

        when(leaseRepository.findById(100))
                .thenReturn(Optional.of(lease));

        when(userRepository.findUserByEmail(
                "owner@test.com"))
                .thenReturn(owner);

        when(leaseRepository.save(any()))
                .thenReturn(lease);

        try (MockedStatic<LeaseMapper> mapper =
                     mockStatic(LeaseMapper.class)) {

            mapper.when(() ->
                    LeaseMapper.convertToLeaseOutputDto(lease))
                    .thenReturn(dto);

            LeaseOutputDTO result =
                    service.updateLeaseStatus(
                            100,
                            "Pending");

            assertNotNull(result);

            verify(invoiceRepository, never())
                    .saveAll(any());
        }
    }

    @Test
    void updateLeaseStatus_Agreed_GenerateInvoices() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        LeaseOutputDTO dto = new LeaseOutputDTO();

        Invoice invoice1 = new Invoice();
        InvoiceOutputDTO invoiceDto = new InvoiceOutputDTO();

        when(leaseRepository.findById(100))
                .thenReturn(Optional.of(lease));

        when(userRepository.findUserByEmail(
                "owner@test.com"))
                .thenReturn(owner);

        when(leaseRepository.save(any()))
                .thenReturn(lease);

        when(tenantProfileRepository.findById(10))
                .thenReturn(Optional.of(tenant));

        try (MockedStatic<LeaseMapper> leaseMapper =
                     mockStatic(LeaseMapper.class);
             MockedStatic<InvoiceMapper> invoiceMapper =
                     mockStatic(InvoiceMapper.class)) {

            leaseMapper.when(() ->
                    LeaseMapper.convertToLeaseOutputDto(lease))
                    .thenReturn(dto);

            invoiceMapper.when(() ->
                    InvoiceMapper.convertToInvoice(
                            any(InvoiceInputDTO.class),
                            eq(tenant),
                            eq(lease)))
                    .thenReturn(invoice1);

            invoiceMapper.when(() ->
                    InvoiceMapper.convertToInvoiceOutputDto(any()))
                    .thenReturn(invoiceDto);

            LeaseOutputDTO result =
                    service.updateLeaseStatus(
                            100,
                            "Agreed");

            assertNotNull(result);

            verify(invoiceRepository)
                    .saveAll(anyList());
        }
    }

    @Test
    void updateLeaseStatus_Agreed_TenantMissingInGenerateInvoice() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        when(leaseRepository.findById(100))
                .thenReturn(Optional.of(lease));

        when(userRepository.findUserByEmail(
                "owner@test.com"))
                .thenReturn(owner);

        when(leaseRepository.save(any()))
                .thenReturn(lease);

        when(tenantProfileRepository.findById(10))
                .thenReturn(Optional.empty());

        assertThrows(TenantIdNotFoundException.class,
                () ->
                service.updateLeaseStatus(
                        100,
                        "Agreed"));
    }

    @Test
    void updateLeaseStatus_Agreed_LeaseMissingInsideInvoiceGeneration() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "owner@test.com",
                        null));

        when(leaseRepository.findById(100))
                .thenReturn(Optional.of(lease))
                .thenReturn(Optional.empty());

        when(userRepository.findUserByEmail(
                "owner@test.com"))
                .thenReturn(owner);

        when(leaseRepository.save(any()))
                .thenReturn(lease);

        when(tenantProfileRepository.findById(10))
                .thenReturn(Optional.of(tenant));

        assertThrows(RuntimeException.class,
                () ->
                service.updateLeaseStatus(
                        100,
                        "Agreed"));
    }
}