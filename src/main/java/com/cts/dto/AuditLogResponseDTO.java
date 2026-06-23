package com.cts.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogResponseDTO {

    private Long auditId;
    private Integer userId;
    private String action;
    private String resourceType;
    private String resourceId;
    private String details;
    private String status;
    private LocalDateTime timestamp;
}
