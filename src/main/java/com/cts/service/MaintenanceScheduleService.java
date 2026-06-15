// MaintenanceScheduleService.java
package com.cts.service;
 
import com.cts.dto.ManagerAssignRequestDTO;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TechnicianStatusUpdateDTO;
import com.cts.dto.TenantIssueRequestDTO;
 
import jakarta.validation.Valid;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
 
public interface MaintenanceScheduleService {
 
    MaintenanceScheduleResponseDTO createByTenant(@Valid TenantIssueRequestDTO requestDTO);
 
    MaintenanceScheduleResponseDTO assignByManager(int scheduleId,
            @Valid ManagerAssignRequestDTO requestDTO);
 
    MaintenanceScheduleResponseDTO updateByTechnician(int scheduleId,
            int technicianId, @Valid TechnicianStatusUpdateDTO requestDTO);
 
    MaintenanceScheduleResponseDTO getScheduleById(int scheduleId);
 
    Page<MaintenanceScheduleResponseDTO> getAllSchedules(String status, String severity,
            Pageable pageable);
 
}