
package com.cts.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MaintenanceLogResponseDTO {

    private int logId;
    private int scheduleId;
    private String remarks;
    private LocalDateTime logDate;
}