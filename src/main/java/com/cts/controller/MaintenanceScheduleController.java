package com.cts.controller;

import com.cts.dto.ManagerAssignRequestDTO;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TechnicianStatusUpdateDTO;
import com.cts.dto.TenantIssueRequestDTO;
import com.cts.service.MaintenanceScheduleService;

import java.util.List;

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
    @Operation(summary = "Tenant raises issue")
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
            @RequestParam int userId,
            @Valid @RequestBody TechnicianStatusUpdateDTO requestDTO) {

        MaintenanceScheduleResponseDTO response = scheduleService.updateByTechnician(scheduleId, userId, requestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "Get schedule by ID",
            description = "Returns maintenance schedule based on schedule ID")
    @PreAuthorize("hasAnyRole('OWNER', 'TENANT')")
    public ResponseEntity<MaintenanceScheduleResponseDTO> getScheduleById(
            @PathVariable int scheduleId) {

        MaintenanceScheduleResponseDTO response = scheduleService.getScheduleById(scheduleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
     }

     @GetMapping
     @Operation(summary = "Get all schedules",
             description = "Returns maintenance schedule list with optional filters")
     @PreAuthorize("hasAnyRole('OWNER', 'TENANT')")
    public ResponseEntity<List<MaintenanceScheduleResponseDTO>> getAllSchedules(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity) {

        List<MaintenanceScheduleResponseDTO> response =
                scheduleService.getAllSchedules(status, severity);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

