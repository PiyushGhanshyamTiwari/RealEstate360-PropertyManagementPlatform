package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.AuditLogResponseDTO;
import com.cts.service.AuditLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/audit-logs")
@AllArgsConstructor
@Slf4j
@Tag(name = "Audit Log Controller",
     description = "Read-only access to audit trail filtered by user, action, or resource type.")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all audit logs")
    public ResponseEntity<List<AuditLogResponseDTO>> getAll() {
        return new ResponseEntity<>(auditLogService.getAllLogs(), HttpStatus.OK);
    }

    @GetMapping("/{auditId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get audit log by AuditID")
    public ResponseEntity<AuditLogResponseDTO> getById(@PathVariable Long auditId) {
        return new ResponseEntity<>(auditLogService.getLogById(auditId), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get audit logs by UserID")
    public ResponseEntity<List<AuditLogResponseDTO>> getByUser(@PathVariable Integer userId) {
        return new ResponseEntity<>(auditLogService.getLogsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get audit logs by action (e.g. REGISTER_USER)")
    public ResponseEntity<List<AuditLogResponseDTO>> getByAction(@PathVariable String action) {
        return new ResponseEntity<>(auditLogService.getLogsByAction(action), HttpStatus.OK);
    }

    @GetMapping("/resource/{resourceType}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get audit logs by resource type (e.g. Property, Lease)")
    public ResponseEntity<List<AuditLogResponseDTO>> getByResourceType(
            @PathVariable String resourceType) {
        return new ResponseEntity<>(auditLogService.getLogsByResourceType(resourceType), HttpStatus.OK);
    }
}
