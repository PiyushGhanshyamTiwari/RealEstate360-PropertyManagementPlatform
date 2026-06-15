
package com.cts.dto;

import lombok.*;

@Data
public class MaintenanceScheduleRequestDTO {
    private int technicianId;
    private String severity;
    private String status;
}