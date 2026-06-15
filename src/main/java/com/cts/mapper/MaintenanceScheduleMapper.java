// MaintenanceScheduleMapper.java
package com.cts.mapper;
 
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TenantIssueRequestDTO;
import com.cts.entity.MaintenanceSchedule;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.enums.MaintenanceStatus;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
 
@Component
public class MaintenanceScheduleMapper {
 
    public MaintenanceSchedule convertToMaintenanceSchedule(
            TenantIssueRequestDTO requestDTO, TenantProfile tenant, Unit unit) {
 
        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setTenant(tenant);
        schedule.setUnit(unit);
        schedule.setIssueDescription(requestDTO.getIssueDescription());
        schedule.setStatus(MaintenanceStatus.OPEN);
        schedule.setScheduledDate(LocalDate.now());
        return schedule;
    }
 
    public MaintenanceScheduleResponseDTO convertToResponseDTO(MaintenanceSchedule schedule) {
        MaintenanceScheduleResponseDTO response = new MaintenanceScheduleResponseDTO();

        response.setScheduleId(schedule.getScheduleId());

        response.setTenantId(
                schedule.getTenant() != null ? schedule.getTenant().getTenantId() : null
        );
        

        response.setUnitId(
                schedule.getUnit() != null
                        ? schedule.getUnit().getUnitId()
                        : null
        );

        response.setIssueDescription(schedule.getIssueDescription());

        response.setTechnicianId(
                schedule.getTechnician() != null
                        ? schedule.getTechnician().getTechnicianId()
                        : null
        );

        response.setSeverity(
                schedule.getSeverity() != null
                        ? schedule.getSeverity().name()
                        : null
        );

        response.setStatus(
                schedule.getStatus() != null
                        ? schedule.getStatus().name()
                        : null
        );

        response.setScheduledDate(schedule.getScheduledDate());
        response.setCreatedAt(schedule.getCreatedAt());
        response.setUpdatedAt(schedule.getUpdatedAt());

        return response;
    }
}
 