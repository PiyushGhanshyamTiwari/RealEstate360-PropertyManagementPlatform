package com.cts.serviceimpl;

import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;
import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.mapper.LeaseMapper;
import com.cts.repository.LeaseRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
import com.cts.serviceimpl.LeaseServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LeaseServiceImplTest {

    @Mock
    private LeaseRepository leaseRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private TenantProfileRepository tenantProfileRepository;

    @InjectMocks
    private LeaseServiceImpl leaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLeaseGeneration_Success() {
        LeaseInputDTO inputDto = new LeaseInputDTO();
        inputDto.setUnitId(1);
        inputDto.setTenantId(10);

        Unit unit = new Unit();
        unit.setUnitId(1);

        TenantProfile tenant = new TenantProfile();
        tenant.setTenantId(10);

        Lease lease = new Lease();
        lease.setLeaseId(100);

        LeaseOutputDTO outputDto = new LeaseOutputDTO();
        outputDto.setLeaseId(100);

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(tenantProfileRepository.findById(10)).thenReturn(Optional.of(tenant));
        when(leaseRepository.save(any(Lease.class))).thenReturn(lease);

        try (var mocked = mockStatic(LeaseMapper.class)) {
            mocked.when(() -> LeaseMapper.convertToLease(inputDto, unit, tenant)).thenReturn(lease);
            mocked.when(() -> LeaseMapper.convertToLeaseOutputDto(lease)).thenReturn(outputDto);

            LeaseOutputDTO result = leaseService.leaseGeneration(inputDto);

            assertNotNull(result);
            assertEquals(100, result.getLeaseId());
            verify(leaseRepository, times(1)).save(lease);
        }
    }

    @Test
    void testLeaseGeneration_UnitNotFound() {
        LeaseInputDTO inputDto = new LeaseInputDTO();
        inputDto.setUnitId(99);
        inputDto.setTenantId(10);

        when(unitRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UnitIdNotFoundException.class,
                () -> leaseService.leaseGeneration(inputDto));
    }

    @Test
    void testLeaseGeneration_TenantNotFound() {
        LeaseInputDTO inputDto = new LeaseInputDTO();
        inputDto.setUnitId(1);
        inputDto.setTenantId(99);

        Unit unit = new Unit();
        unit.setUnitId(1);

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(tenantProfileRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(TenantIdNotFoundException.class,
                () -> leaseService.leaseGeneration(inputDto));
    }

    @Test
    void testUpdateLeaseStatus_Success() {
        Lease lease = new Lease();
        lease.setLeaseId(100);
        lease.setStatus(Lease.Status.Review);

        Lease updatedLease = new Lease();
        updatedLease.setLeaseId(100);
        updatedLease.setStatus(Lease.Status.Agreed);

        LeaseOutputDTO dto = new LeaseOutputDTO();
        dto.setLeaseId(100);
        dto.setStatus("Agreed");

        when(leaseRepository.findById(100)).thenReturn(Optional.of(lease));
        when(leaseRepository.save(lease)).thenReturn(updatedLease);

        try (var mocked = mockStatic(LeaseMapper.class)) {
            mocked.when(() -> LeaseMapper.convertToLeaseOutputDto(updatedLease)).thenReturn(dto);

            LeaseOutputDTO result = leaseService.updateLeaseStatus(100, "Agreed");

            assertEquals("Agreed", result.getStatus());
            verify(leaseRepository, times(1)).save(lease);
        }
    }

    @Test
    void testUpdateLeaseStatus_NotFound() {
        when(leaseRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> leaseService.updateLeaseStatus(999, "Cancelled"));
    }
}
