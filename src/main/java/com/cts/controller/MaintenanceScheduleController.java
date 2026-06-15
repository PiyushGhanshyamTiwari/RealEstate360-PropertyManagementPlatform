package com.cts.controller;
 
import com.cts.dto.ManagerAssignRequestDTO;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TechnicianStatusUpdateDTO;
import com.cts.dto.TenantIssueRequestDTO;
import com.cts.service.MaintenanceScheduleService;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
 
@RestController
@RequestMapping("/maintenance-schedules")
@AllArgsConstructor
@Tag(name = "Maintenance Schedule Controller", description = "Operations related to Maintenance Schedules")
public class MaintenanceScheduleController {
 
    private final MaintenanceScheduleService scheduleService;
 
    @PostMapping("/tenant")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<?> createByTenant(
            @Valid @RequestBody TenantIssueRequestDTO requestDTO) {
        MaintenanceScheduleResponseDTO response = scheduleService.createByTenant(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{scheduleId}/assign")
    @Operation(summary = "Manager assigns technician",
            description = "Property manager assigns technician to a schedule")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MaintenanceScheduleResponseDTO> assignByManager(
            @PathVariable int scheduleId,
            @Valid @RequestBody ManagerAssignRequestDTO requestDTO) {
 
        MaintenanceScheduleResponseDTO response = scheduleService.assignByManager(scheduleId, requestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
 
    @PutMapping("/{scheduleId}/status")
    @Operation(summary = "Technician updates status",
            description = "Technician updates the status - must provide technicianId to verify identity")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<MaintenanceScheduleResponseDTO> updateByTechnician(
            @PathVariable int scheduleId,
            @RequestParam int technicianId,
            @Valid @RequestBody TechnicianStatusUpdateDTO requestDTO) {
 
        MaintenanceScheduleResponseDTO response = scheduleService.updateByTechnician(scheduleId, technicianId, requestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
 
    @GetMapping("/{scheduleId}")
    @Operation(summary = "Get schedule by ID",
            description = "Returns maintenance schedule based on schedule ID")
    @PreAuthorize("hasAnyRole('TECHNICIAN','OWNER')")
    public ResponseEntity<MaintenanceScheduleResponseDTO> getScheduleById(
            @PathVariable int scheduleId) {
 
        MaintenanceScheduleResponseDTO response = scheduleService.getScheduleById(scheduleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
 
    @GetMapping
    @Operation(summary = "Get all schedules",
            description = "Returns paginated maintenance schedule list with optional filters")
    @PreAuthorize("hasAnyRole('TECHNICIAN','OWNER')")
    public ResponseEntity<Page<MaintenanceScheduleResponseDTO>> getAllSchedules(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
 
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<MaintenanceScheduleResponseDTO> response =
                scheduleService.getAllSchedules(status, severity, pageable);
 
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
 