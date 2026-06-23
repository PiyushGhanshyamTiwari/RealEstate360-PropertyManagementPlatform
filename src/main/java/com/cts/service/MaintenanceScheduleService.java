// MaintenanceScheduleService.java
package com.cts.service;

import com.cts.dto.MaintenanceScheduleRequestDTO;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.ManagerAssignRequestDTO;
import com.cts.dto.TechnicianStatusUpdateDTO;
import com.cts.dto.TenantIssueRequestDTO;
import jakarta.validation.Valid;
import java.util.List;


public interface MaintenanceScheduleService {

	MaintenanceScheduleResponseDTO createByTenant(@Valid TenantIssueRequestDTO requestDTO);

    MaintenanceScheduleResponseDTO assignByManager(int scheduleId,
                                                   @Valid ManagerAssignRequestDTO requestDTO);

    MaintenanceScheduleResponseDTO updateByTechnician(int scheduleId,
                                                      int userId, @Valid TechnicianStatusUpdateDTO requestDTO);

    MaintenanceScheduleResponseDTO getScheduleById(int scheduleId);

    List<MaintenanceScheduleResponseDTO> getAllSchedules(String status, String severity);
	
}