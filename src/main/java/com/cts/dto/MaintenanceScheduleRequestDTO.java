
package com.cts.dto;

import lombok.*;

@Data
public class MaintenanceScheduleRequestDTO {

	 private Integer userId;
	 private String severity;
	 private String status;
}