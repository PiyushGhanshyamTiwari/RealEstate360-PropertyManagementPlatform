// MaintenanceScheduleServiceImpl.java
package com.cts.serviceimpl;
 
import com.cts.dto.ManagerAssignRequestDTO;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TechnicianStatusUpdateDTO;
import com.cts.dto.TenantIssueRequestDTO;
import com.cts.entity.MaintenanceSchedule;
import com.cts.entity.TenantProfile;
import com.cts.entity.Technician;
import com.cts.entity.Unit;
import com.cts.enums.MaintenanceStatus;
import com.cts.enums.Severity;
import com.cts.enums.TechnicianStatus;
import com.cts.exception.InvalidStatusTransitionException;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.exception.NoTechnicianAssignedException;
import com.cts.exception.TechnicianInactiveException;
import com.cts.exception.TechnicianNotAvailableException;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.TechnicianNotFoundException;
import com.cts.exception.UnauthorizedTechnicianException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.mapper.MaintenanceScheduleMapper;
import com.cts.repository.MaintenanceScheduleRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
import com.cts.service.MaintenanceScheduleService;
import com.cts.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
@Service
@RequiredArgsConstructor
public class MaintenanceScheduleServiceImpl implements MaintenanceScheduleService {
 
    private final MaintenanceScheduleRepository scheduleRepository;
    private final TenantProfileRepository tenantRepository;
    private final UnitRepository unitRepository;
    private final TechnicianRepository technicianRepository;
    private final MaintenanceScheduleMapper mapper;
 
    @Override
    @Transactional
    public MaintenanceScheduleResponseDTO createByTenant(TenantIssueRequestDTO requestDTO) {
        TenantProfile tenant = tenantRepository.findById(requestDTO.getTenantId())
                .orElseThrow(() -> new TenantIdNotFoundException(
                        "Tenant not found with ID: " + requestDTO.getTenantId()));
 
        Unit unit = unitRepository.findById(requestDTO.getUnitId())
                .orElseThrow(() -> new UnitIdNotFoundException(
                        "Unit not found with ID: " + requestDTO.getUnitId()));
 
        MaintenanceSchedule schedule = mapper.convertToMaintenanceSchedule(requestDTO, tenant, unit);
        return mapper.convertToResponseDTO(scheduleRepository.save(schedule));
    }
 
    @Override
    @Transactional
    public MaintenanceScheduleResponseDTO assignByManager(int scheduleId,
            ManagerAssignRequestDTO requestDTO) {
 
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new MaintenanceScheduleNotFoundException(scheduleId));
 
        if (!schedule.getStatus().equals(MaintenanceStatus.OPEN)) {
            throw new InvalidStatusTransitionException(
                    schedule.getStatus().name(), MaintenanceStatus.ASSIGNED.name());
        }
 
        Technician technician = technicianRepository.findById(requestDTO.getTechnicianId())
                .orElseThrow(() -> new TechnicianNotFoundException(
                        "Technician not found with ID: " + requestDTO.getTechnicianId()));
 
        // Check if technician is active (not left company)
        if (!technician.getStatus().equals(TechnicianStatus.ACTIVE)) {
            throw new TechnicianInactiveException(
                    "Cannot assign - technician is inactive");
        }
 
        // Check if technician is available
        if (!technician.getAvailable()) {
            throw new TechnicianNotAvailableException(
                    "Technician is not available - already assigned to another task");
        }
 
        schedule.setTechnician(technician);
        schedule.setSeverity(Severity.valueOf(requestDTO.getSeverity().toUpperCase()));
        schedule.setStatus(MaintenanceStatus.ASSIGNED);
 
        // Mark technician as unavailable
        technician.setAvailable(false);
        technicianRepository.save(technician);
 
        return mapper.convertToResponseDTO(scheduleRepository.save(schedule));
    }
 
    @Override
    @Transactional
    public MaintenanceScheduleResponseDTO updateByTechnician(int scheduleId,
            int technicianId, TechnicianStatusUpdateDTO requestDTO) {
 
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new MaintenanceScheduleNotFoundException(scheduleId));
 
        // Check if technician is assigned
        if (schedule.getTechnician() == null) {
            throw new NoTechnicianAssignedException(
                    "Cannot update status - no technician assigned to this schedule");
        }
 
        Technician assignedTechnician = schedule.getTechnician();
 
        // Verify that the technician updating is the one assigned
        if (assignedTechnician.getTechnicianId() != technicianId) {
            throw new UnauthorizedTechnicianException(
                    "Access denied - you can only update schedules assigned to you");
        }
 
        // Check if technician is still active
        if (!assignedTechnician.getStatus().equals(TechnicianStatus.ACTIVE)) {
            throw new TechnicianInactiveException(
                    "Cannot update status - technician is no longer active");
        }
 
        MaintenanceStatus newStatus = MaintenanceStatus.valueOf(
                requestDTO.getStatus().toUpperCase());
 
        if (schedule.getStatus().equals(MaintenanceStatus.ASSIGNED)
                && !newStatus.equals(MaintenanceStatus.IN_PROGRESS)) {
            throw new InvalidStatusTransitionException(
                    schedule.getStatus().name(), newStatus.name());
        }
        if (schedule.getStatus().equals(MaintenanceStatus.IN_PROGRESS)
                && !newStatus.equals(MaintenanceStatus.RESOLVED)) {
            throw new InvalidStatusTransitionException(
                    schedule.getStatus().name(), newStatus.name());
        }
        if (schedule.getStatus().equals(MaintenanceStatus.RESOLVED)
                && !newStatus.equals(MaintenanceStatus.CLOSED)) {
            throw new InvalidStatusTransitionException(
                    schedule.getStatus().name(), newStatus.name());
        }
        if (schedule.getStatus().equals(MaintenanceStatus.CLOSED)) {
            throw new InvalidStatusTransitionException(
                    schedule.getStatus().name(), newStatus.name());
        }
 
        schedule.setStatus(newStatus);
 
        // If work completed, free up the technician
        if (newStatus.equals(MaintenanceStatus.RESOLVED) ||
            newStatus.equals(MaintenanceStatus.CLOSED)) {
 
            Technician technician = schedule.getTechnician();
            if (technician != null) {
                technician.setAvailable(true);
                technicianRepository.save(technician);
            }
        }
 
        return mapper.convertToResponseDTO(scheduleRepository.save(schedule));
    }
 
    @Override
    public MaintenanceScheduleResponseDTO getScheduleById(int scheduleId) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new MaintenanceScheduleNotFoundException(scheduleId));
        return mapper.convertToResponseDTO(schedule);
    }
 
    @Override
    public Page<MaintenanceScheduleResponseDTO> getAllSchedules(String status, String severity,
            Pageable pageable) {
 
        if (status != null && !status.isBlank()) {
            return scheduleRepository
                    .findByStatus(MaintenanceStatus.valueOf(status.toUpperCase()), pageable)
                    .map(schedule -> mapper.convertToResponseDTO(schedule));
        }
 
        if (severity != null && !severity.isBlank()) {
            return scheduleRepository
                    .findBySeverity(Severity.valueOf(severity.toUpperCase()), pageable)
                    .map(schedule -> mapper.convertToResponseDTO(schedule));
        }
 
        return scheduleRepository.findAll(pageable)
                .map(schedule -> mapper.convertToResponseDTO(schedule));
    }
}
 