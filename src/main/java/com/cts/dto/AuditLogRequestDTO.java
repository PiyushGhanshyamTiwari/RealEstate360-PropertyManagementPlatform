package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogRequestDTO {

    private Integer userId;
    private String action;
    private String resourceType;
    private String resourceId;
    private String details;
    private String status;  // SUCCESS or FAILED
}
