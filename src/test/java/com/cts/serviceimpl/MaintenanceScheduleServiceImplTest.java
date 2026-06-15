package com.cts.serviceimpl;
 
import com.cts.dto.ManagerAssignRequestDTO;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TechnicianStatusUpdateDTO;
import com.cts.dto.TenantIssueRequestDTO;
import com.cts.entity.MaintenanceSchedule;
import com.cts.entity.Technician;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.enums.MaintenanceStatus;
import com.cts.enums.Severity;
import com.cts.enums.TechnicianStatus;
import com.cts.exception.InvalidStatusTransitionException;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.exception.NoTechnicianAssignedException;
import com.cts.exception.TechnicianInactiveException;
import com.cts.exception.TechnicianNotAvailableException;
import com.cts.exception.TechnicianNotFoundException;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnauthorizedTechnicianException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.mapper.MaintenanceScheduleMapper;
import com.cts.repository.MaintenanceScheduleRepository;
import com.cts.repository.TechnicianRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
 
import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
public class MaintenanceScheduleServiceImplTest {
 
    @Mock
    private MaintenanceScheduleRepository scheduleRepository;
 
    @Mock
    private TenantProfileRepository tenantRepository;
 
    @Mock
    private UnitRepository unitRepository;
 
    @Mock
    private TechnicianRepository technicianRepository;
 
    @Mock
    private MaintenanceScheduleMapper mapper;
 
    @InjectMocks
    private MaintenanceScheduleServiceImpl service;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
 
    private Technician activeAvailableTechnician(int id) {
        Technician t = new Technician();
        t.setTechnicianId(id);
        t.setStatus(TechnicianStatus.ACTIVE);
        t.setAvailable(true);
        return t;
    }
 
    // ============== createByTenant ==============
 
    @Test
    void testCreateByTenant_Success() {
        TenantIssueRequestDTO request = new TenantIssueRequestDTO();
        request.setTenantId(1);
        request.setUnitId(101);
        request.setIssueDescription("Leak");
 
        TenantProfile tenant = new TenantProfile();
        tenant.setTenantId(1);
 
        Unit unit = new Unit();
        unit.setUnitId(101);
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        MaintenanceScheduleResponseDTO response = new MaintenanceScheduleResponseDTO();
 
        when(tenantRepository.findById(1)).thenReturn(Optional.of(tenant));
        when(unitRepository.findById(101)).thenReturn(Optional.of(unit));
        when(mapper.convertToMaintenanceSchedule(request, tenant, unit)).thenReturn(schedule);
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule)).thenReturn(response);
 
        MaintenanceScheduleResponseDTO result = service.createByTenant(request);
 
        assertNotNull(result);
        verify(scheduleRepository, times(1)).save(schedule);
    }
 
    @Test
    void testCreateByTenant_TenantNotFound() {
        TenantIssueRequestDTO request = new TenantIssueRequestDTO();
        request.setTenantId(99);
        request.setUnitId(101);
 
        when(tenantRepository.findById(99)).thenReturn(Optional.empty());
 
        assertThrows(TenantIdNotFoundException.class,
                () -> service.createByTenant(request));
    }
 
    @Test
    void testCreateByTenant_UnitNotFound() {
        TenantIssueRequestDTO request = new TenantIssueRequestDTO();
        request.setTenantId(1);
        request.setUnitId(999);
 
        TenantProfile tenant = new TenantProfile();
        tenant.setTenantId(1);
 
        when(tenantRepository.findById(1)).thenReturn(Optional.of(tenant));
        when(unitRepository.findById(999)).thenReturn(Optional.empty());
 
        assertThrows(UnitIdNotFoundException.class,
                () -> service.createByTenant(request));
    }
 
    // ============== assignByManager ==============
 
    @Test
    void testAssignByManager_Success() {
        ManagerAssignRequestDTO request = new ManagerAssignRequestDTO();
        request.setTechnicianId(5);
        request.setSeverity("HIGH");
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.OPEN);
 
        Technician technician = activeAvailableTechnician(5);
        MaintenanceScheduleResponseDTO response = new MaintenanceScheduleResponseDTO();
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findById(5)).thenReturn(Optional.of(technician));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule)).thenReturn(response);
 
        MaintenanceScheduleResponseDTO result = service.assignByManager(1, request);
 
        assertNotNull(result);
        assertEquals(MaintenanceStatus.ASSIGNED, schedule.getStatus());
        assertFalse(technician.getAvailable());
        verify(technicianRepository, times(1)).save(technician);
    }
 
    @Test
    void testAssignByManager_ScheduleNotFound() {
        ManagerAssignRequestDTO request = new ManagerAssignRequestDTO();
        request.setTechnicianId(5);
 
        when(scheduleRepository.findById(99)).thenReturn(Optional.empty());
 
        assertThrows(MaintenanceScheduleNotFoundException.class,
                () -> service.assignByManager(99, request));
    }
 
    @Test
    void testAssignByManager_InvalidStatusTransition() {
        ManagerAssignRequestDTO request = new ManagerAssignRequestDTO();
        request.setTechnicianId(5);
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.RESOLVED);
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
 
        assertThrows(InvalidStatusTransitionException.class,
                () -> service.assignByManager(1, request));
    }
 
    @Test
    void testAssignByManager_TechnicianNotFound() {
        ManagerAssignRequestDTO request = new ManagerAssignRequestDTO();
        request.setTechnicianId(99);
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.OPEN);
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findById(99)).thenReturn(Optional.empty());
 
        assertThrows(TechnicianNotFoundException.class,
                () -> service.assignByManager(1, request));
    }
 
    @Test
    void testAssignByManager_TechnicianInactive() {
        ManagerAssignRequestDTO request = new ManagerAssignRequestDTO();
        request.setTechnicianId(5);
        request.setSeverity("HIGH");
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.OPEN);
 
        Technician technician = new Technician();
        technician.setTechnicianId(5);
        technician.setStatus(TechnicianStatus.INACTIVE);
        technician.setAvailable(true);
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findById(5)).thenReturn(Optional.of(technician));
 
        assertThrows(TechnicianInactiveException.class,
                () -> service.assignByManager(1, request));
    }
 
    @Test
    void testAssignByManager_TechnicianNotAvailable() {
        ManagerAssignRequestDTO request = new ManagerAssignRequestDTO();
        request.setTechnicianId(5);
        request.setSeverity("HIGH");
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.OPEN);
 
        Technician technician = new Technician();
        technician.setTechnicianId(5);
        technician.setStatus(TechnicianStatus.ACTIVE);
        technician.setAvailable(false);
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findById(5)).thenReturn(Optional.of(technician));
 
        assertThrows(TechnicianNotAvailableException.class,
                () -> service.assignByManager(1, request));
    }
 
    // ============== updateByTechnician ==============
 
    @Test
    void testUpdateByTechnician_AssignedToInProgress() {
        TechnicianStatusUpdateDTO request = new TechnicianStatusUpdateDTO();
        request.setStatus("IN_PROGRESS");
 
        Technician technician = activeAvailableTechnician(3);
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.ASSIGNED);
        schedule.setTechnician(technician);
 
        MaintenanceScheduleResponseDTO response = new MaintenanceScheduleResponseDTO();
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule)).thenReturn(response);
 
        MaintenanceScheduleResponseDTO result = service.updateByTechnician(1, 3, request);
 
        assertNotNull(result);
        assertEquals(MaintenanceStatus.IN_PROGRESS, schedule.getStatus());
    }
 
    @Test
    void testUpdateByTechnician_NoTechnicianAssigned() {
        TechnicianStatusUpdateDTO request = new TechnicianStatusUpdateDTO();
        request.setStatus("IN_PROGRESS");
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.OPEN);
        schedule.setTechnician(null);
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
 
        assertThrows(NoTechnicianAssignedException.class,
                () -> service.updateByTechnician(1, 3, request));
    }
 
    @Test
    void testUpdateByTechnician_UnauthorizedTechnician() {
        TechnicianStatusUpdateDTO request = new TechnicianStatusUpdateDTO();
        request.setStatus("IN_PROGRESS");
 
        Technician assignedTech = activeAvailableTechnician(3);
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.ASSIGNED);
        schedule.setTechnician(assignedTech);
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
 
        // Technician 99 tries to update task assigned to 3
        assertThrows(UnauthorizedTechnicianException.class,
                () -> service.updateByTechnician(1, 99, request));
    }
 
    @Test
    void testUpdateByTechnician_InvalidTransition() {
        TechnicianStatusUpdateDTO request = new TechnicianStatusUpdateDTO();
        request.setStatus("CLOSED");
 
        Technician technician = activeAvailableTechnician(3);
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.ASSIGNED);
        schedule.setTechnician(technician);
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
 
        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateByTechnician(1, 3, request));
    }
 
    // ============== getScheduleById ==============
 
    @Test
    void testGetScheduleById_Success() {
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        MaintenanceScheduleResponseDTO response = new MaintenanceScheduleResponseDTO();
 
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(mapper.convertToResponseDTO(schedule)).thenReturn(response);
 
        MaintenanceScheduleResponseDTO result = service.getScheduleById(1);
 
        assertNotNull(result);
    }
 
    @Test
    void testGetScheduleById_NotFound() {
        when(scheduleRepository.findById(99)).thenReturn(Optional.empty());
 
        assertThrows(MaintenanceScheduleNotFoundException.class,
                () -> service.getScheduleById(99));
    }
 
    // ============== getAllSchedules ==============
 
    @Test
    void testGetAllSchedules_ByStatus() {
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.OPEN);
        MaintenanceScheduleResponseDTO dto = new MaintenanceScheduleResponseDTO();
 
        Page<MaintenanceSchedule> page = new PageImpl<>(List.of(schedule));
 
        when(scheduleRepository.findByStatus(eq(MaintenanceStatus.OPEN), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.convertToResponseDTO(schedule)).thenReturn(dto);
 
        Page<MaintenanceScheduleResponseDTO> result =
                service.getAllSchedules("OPEN", null, Pageable.unpaged());
 
        assertEquals(1, result.getTotalElements());
    }
 
    @Test
    void testGetAllSchedules_BySeverity() {
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setSeverity(Severity.HIGH);
        MaintenanceScheduleResponseDTO dto = new MaintenanceScheduleResponseDTO();
 
        Page<MaintenanceSchedule> page = new PageImpl<>(List.of(schedule));
 
        when(scheduleRepository.findBySeverity(eq(Severity.HIGH), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.convertToResponseDTO(schedule)).thenReturn(dto);
 
        Page<MaintenanceScheduleResponseDTO> result =
                service.getAllSchedules(null, "HIGH", Pageable.unpaged());
 
        assertEquals(1, result.getTotalElements());
    }
 
    @Test
    void testGetAllSchedules_Default() {
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        MaintenanceScheduleResponseDTO dto = new MaintenanceScheduleResponseDTO();
 
        Page<MaintenanceSchedule> page = new PageImpl<>(List.of(schedule));
 
        when(scheduleRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.convertToResponseDTO(schedule)).thenReturn(dto);
 
        Page<MaintenanceScheduleResponseDTO> result =
                service.getAllSchedules(null, null, Pageable.unpaged());
 
        assertEquals(1, result.getTotalElements());
    }
}