
package com.cts.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MaintenanceScheduleResponseDTO {

	 private int scheduleId;
	    private int userId;//tenant userId
	    private Integer unitId;
	    private Integer technicianUserId;
	    private String issueDescription;
	    private String category;
	    private String severity;
	    private String status;
	    private LocalDate scheduledDate;
	    private LocalDateTime createdAt;
	    private LocalDateTime updatedAt;
	
}