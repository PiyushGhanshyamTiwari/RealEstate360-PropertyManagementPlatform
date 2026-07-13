package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enums.*;
import com.cts.exception.*;
import com.cts.mapper.MaintenanceScheduleMapper;
import com.cts.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MaintenanceScheduleServiceImplTest {

    @Mock private MaintenanceScheduleRepository scheduleRepository;
    @Mock private TenantProfileRepository tenantRepository;
    @Mock private UnitRepository unitRepository;
    @Mock private TechnicianRepository technicianRepository;
    @Mock private MaintenanceScheduleMapper mapper;

    @InjectMocks
    private MaintenanceScheduleServiceImpl service;

    private TenantProfile tenant;
    private Unit unit;
    private MaintenanceSchedule schedule;
    private Technician technician;

    @BeforeEach
    void setup() {
        tenant = new TenantProfile();
        unit = new Unit();

        schedule = new MaintenanceSchedule();
        schedule.setStatus(MaintenanceStatus.OPEN);

        technician = new Technician();
        User user = new User();
        user.setUserId(10);
        technician.setUser(user);
        technician.setSpecialization(TechnicianSpecialization.ELECTRICIAN);
    }

  
    @Test
    void testCreateByTenantSuccess() {
        TenantIssueRequestDTO request = new TenantIssueRequestDTO();
        request.setUserId(1);
        request.setUnitId(1);

        when(tenantRepository.findByUser_UserId(1)).thenReturn(Optional.of(tenant));
        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(mapper.convertToMaintenanceSchedule(request, tenant, unit))
                .thenReturn(schedule);
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        MaintenanceScheduleResponseDTO result = service.createByTenant(request);

        assertNotNull(result);
    }

    
    @Test
    void testCreateByTenantTenantNotFound() {
        TenantIssueRequestDTO request = new TenantIssueRequestDTO();
        request.setUserId(1);

        when(tenantRepository.findByUser_UserId(1)).thenReturn(Optional.empty());

        assertThrows(TenantIdNotFoundException.class,
                () -> service.createByTenant(request));
    }

    
    @Test
    void testCreateByTenantUnitNotFound() {
        TenantIssueRequestDTO request = new TenantIssueRequestDTO();
        request.setUserId(1);
        request.setUnitId(1);

        when(tenantRepository.findByUser_UserId(1)).thenReturn(Optional.of(tenant));
        when(unitRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(UnitIdNotFoundException.class,
                () -> service.createByTenant(request));
    }

    
    @Test
    void testAssignByManagerSuccess() {
        schedule.setCategory(IssueCategory.ELECTRICAL);

        ManagerAssignRequestDTO req = new ManagerAssignRequestDTO();
        req.setUserId(10);
        req.setSeverity("HIGH");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findByUser_UserId(10)).thenReturn(Optional.of(technician));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        MaintenanceScheduleResponseDTO result =
                service.assignByManager(1, req);

        assertNotNull(result);
    }

    
    @Test
    void testAssignByManagerInvalidStatus() {
        schedule.setStatus(MaintenanceStatus.ASSIGNED);

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.assignByManager(1, new ManagerAssignRequestDTO()));
    }

    
    @Test
    void testAssignByManagerTechnicianNotFound() {
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findByUser_UserId(10)).thenReturn(Optional.empty());

        ManagerAssignRequestDTO req = new ManagerAssignRequestDTO();
        req.setUserId(10);

        assertThrows(TechnicianNotFoundException.class,
                () -> service.assignByManager(1, req));
    }

    
    @Test
    void testAssignByManagerSpecializationMismatch() {
        schedule.setCategory(IssueCategory.PLUMBING);

        ManagerAssignRequestDTO req = new ManagerAssignRequestDTO();
        req.setUserId(10);

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findByUser_UserId(10)).thenReturn(Optional.of(technician));

        assertThrows(SpecializationMismatchException.class,
                () -> service.assignByManager(1, req));
    }

    private Technician technicianWith(int userId, TechnicianSpecialization spec) {
        User u = new User();
        u.setUserId(userId);
        Technician t = new Technician();
        t.setUser(u);
        t.setSpecialization(spec);
        return t;
    }

    @Test
    void testAssignByManagerCleaning() {
        schedule.setCategory(IssueCategory.CLEANING);
        Technician tech = technicianWith(20, TechnicianSpecialization.HOUSE_CLEANER);

        ManagerAssignRequestDTO req = new ManagerAssignRequestDTO();
        req.setUserId(20);
        req.setSeverity("LOW");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findByUser_UserId(20)).thenReturn(Optional.of(tech));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        assertNotNull(service.assignByManager(1, req));
    }

    @Test
    void testAssignByManagerPainting() {
        schedule.setCategory(IssueCategory.PAINTING);
        Technician tech = technicianWith(21, TechnicianSpecialization.PAINTER);

        ManagerAssignRequestDTO req = new ManagerAssignRequestDTO();
        req.setUserId(21);
        req.setSeverity("MEDIUM");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findByUser_UserId(21)).thenReturn(Optional.of(tech));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        assertNotNull(service.assignByManager(1, req));
    }

    @Test
    void testAssignByManagerCarpentry() {
        schedule.setCategory(IssueCategory.CARPENTRY);
        Technician tech = technicianWith(22, TechnicianSpecialization.CARPENTER);

        ManagerAssignRequestDTO req = new ManagerAssignRequestDTO();
        req.setUserId(22);
        req.setSeverity("CRITICAL");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findByUser_UserId(22)).thenReturn(Optional.of(tech));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        assertNotNull(service.assignByManager(1, req));
    }

    @Test
    void testAssignByManagerNullCategoryMatchesAny() {
        schedule.setCategory(null);

        ManagerAssignRequestDTO req = new ManagerAssignRequestDTO();
        req.setUserId(10);
        req.setSeverity("LOW");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(technicianRepository.findByUser_UserId(10)).thenReturn(Optional.of(technician));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        assertNotNull(service.assignByManager(1, req));
    }

    @Test
    void testUpdateByTechnicianInProgressToResolved() {
        schedule.setTechnician(technician);
        schedule.setStatus(MaintenanceStatus.IN_PROGRESS);

        TechnicianStatusUpdateDTO req = new TechnicianStatusUpdateDTO();
        req.setStatus("RESOLVED");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        assertNotNull(service.updateByTechnician(1, 10, req));
    }

    @Test
    void testUpdateByTechnicianInProgressInvalid() {
        schedule.setTechnician(technician);
        schedule.setStatus(MaintenanceStatus.IN_PROGRESS);

        TechnicianStatusUpdateDTO req = new TechnicianStatusUpdateDTO();
        req.setStatus("CLOSED");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateByTechnician(1, 10, req));
    }

    @Test
    void testUpdateByTechnicianResolvedToClosed() {
        schedule.setTechnician(technician);
        schedule.setStatus(MaintenanceStatus.RESOLVED);

        TechnicianStatusUpdateDTO req = new TechnicianStatusUpdateDTO();
        req.setStatus("CLOSED");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        assertNotNull(service.updateByTechnician(1, 10, req));
    }

    @Test
    void testUpdateByTechnicianResolvedInvalid() {
        schedule.setTechnician(technician);
        schedule.setStatus(MaintenanceStatus.RESOLVED);

        TechnicianStatusUpdateDTO req = new TechnicianStatusUpdateDTO();
        req.setStatus("IN_PROGRESS");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateByTechnician(1, 10, req));
    }

    @Test
    void testUpdateByTechnicianClosedAlwaysInvalid() {
        schedule.setTechnician(technician);
        schedule.setStatus(MaintenanceStatus.CLOSED);

        TechnicianStatusUpdateDTO req = new TechnicianStatusUpdateDTO();
        req.setStatus("CLOSED");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateByTechnician(1, 10, req));
    }

    
    @Test
    void testUpdateByTechnicianSuccess() {
        schedule.setTechnician(technician);
        schedule.setStatus(MaintenanceStatus.ASSIGNED);

        TechnicianStatusUpdateDTO req = new TechnicianStatusUpdateDTO();
        req.setStatus("IN_PROGRESS");

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(schedule)).thenReturn(schedule);
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        MaintenanceScheduleResponseDTO result =
                service.updateByTechnician(1, 10, req);

        assertNotNull(result);
    }

    
    @Test
    void testUpdateByTechnicianNoTechnician() {
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

        assertThrows(NoTechnicianAssignedException.class,
                () -> service.updateByTechnician(1, 10, new TechnicianStatusUpdateDTO()));
    }

    
    @Test
    void testUpdateByTechnicianUnauthorized() {
        schedule.setTechnician(technician);

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

        assertThrows(UnauthorizedTechnicianException.class,
                () -> service.updateByTechnician(1, 99, new TechnicianStatusUpdateDTO()));
    }

    
    @Test
    void testUpdateByTechnicianInvalidTransition() {
        schedule.setTechnician(technician);
        schedule.setStatus(MaintenanceStatus.ASSIGNED);

        TechnicianStatusUpdateDTO req = new TechnicianStatusUpdateDTO();
        req.setStatus("RESOLVED"); // invalid

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));

        assertThrows(InvalidStatusTransitionException.class,
                () -> service.updateByTechnician(1, 10, req));
    }

    
    @Test
    void testGetScheduleById() {
        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        assertNotNull(service.getScheduleById(1));
    }

   
    @Test
    void testGetScheduleByIdNotFound() {
        when(scheduleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(MaintenanceScheduleNotFoundException.class,
                () -> service.getScheduleById(1));
    }

    
    @Test
    void testGetAllSchedulesByStatus() {
        when(scheduleRepository.findByStatus(MaintenanceStatus.OPEN))
                .thenReturn(List.of(schedule));
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        List<MaintenanceScheduleResponseDTO> result =
                service.getAllSchedules("OPEN", null);

        assertEquals(1, result.size());
    }

    
    @Test
    void testGetAllSchedulesBySeverity() {
        when(scheduleRepository.findBySeverity(Severity.HIGH))
                .thenReturn(List.of(schedule));
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        List<MaintenanceScheduleResponseDTO> result =
                service.getAllSchedules(null, "HIGH");

        assertEquals(1, result.size());
    }

    
    @Test
    void testGetAllSchedulesAll() {
        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));
        when(mapper.convertToResponseDTO(schedule))
                .thenReturn(new MaintenanceScheduleResponseDTO());

        List<MaintenanceScheduleResponseDTO> result =
                service.getAllSchedules(null, null);

        assertEquals(1, result.size());
    }
}
