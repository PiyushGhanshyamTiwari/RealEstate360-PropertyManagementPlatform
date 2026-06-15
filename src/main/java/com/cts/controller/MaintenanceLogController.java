// MaintenanceLogController.java
package com.cts.controller;
 
import com.cts.dto.MaintenanceLogRequestDTO;
import com.cts.dto.MaintenanceLogResponseDTO;
import com.cts.service.MaintenanceLogService;
 
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
 
@RestController
@RequestMapping("/maintenance-logs")
@AllArgsConstructor
@Tag(name = "Maintenance Log Controller", description = "Operations related to Maintenance Logs")
public class MaintenanceLogController {
 
    private final MaintenanceLogService logService;
 
    @PostMapping("/technician")
    @Operation(summary = "Add maintenance log",
            description = "Technician adds a log after completing work")
    @PreAuthorize("hasRole('TECHNICIAN')")
    public ResponseEntity<MaintenanceLogResponseDTO> addLog(
            @RequestBody MaintenanceLogRequestDTO requestDTO) {
 
        MaintenanceLogResponseDTO response = logService.addLog(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
 
    @GetMapping("/schedule/{scheduleId}")
    @Operation(summary = "Get logs by schedule ID",
            description = "Returns all logs for a given maintenance schedule")
    @PreAuthorize("hasAnyRole('TECHNICIAN','OWNER','ADMIN')")
    public ResponseEntity<Page<MaintenanceLogResponseDTO>> getLogsByScheduleId(
            @PathVariable int scheduleId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
 
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<MaintenanceLogResponseDTO> response =
                logService.getLogsByScheduleId(scheduleId, pageable);
 
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
 