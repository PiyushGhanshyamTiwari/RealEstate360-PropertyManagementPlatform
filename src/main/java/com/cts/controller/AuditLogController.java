package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all audit logs")
    public ResponseEntity<List<AuditLogResponseDTO>> getAll(@RequestParam(required = false) String logType,@RequestParam(required = false) String logValue) {
        return new ResponseEntity<>(auditLogService.getAllLogs(logType,logValue), HttpStatus.OK);
    }

}
