package com.cts.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaseOutputDTO {
	private int leaseId;
	private int unitId;
	private int ownerId;
	private String ownerName;
	private int tenantId;
	private String tenantName;
	private LocalDate startDate;
	private LocalDate endDate;
	private double rentAmount;
	private double depositAmount;
	private String status;
	private LocalDateTime createdAt;
	
	}
