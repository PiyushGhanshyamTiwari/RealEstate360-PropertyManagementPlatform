package com.cts.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MaintenanceScheduleResponseDTO {

    private Integer scheduleId;

    // Also boxed for safety; an unboxed null tenant id would fail the same way.
    private Integer tenantId;

    private Integer unitId;
    private Integer technicianId;
    private String issueDescription;
    private String severity;
    private String status;
    private LocalDate scheduledDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}