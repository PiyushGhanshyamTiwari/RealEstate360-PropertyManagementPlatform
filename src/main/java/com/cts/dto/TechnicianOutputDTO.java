package com.cts.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cts.enums.TechnicianSpecialization;
import com.cts.enums.TechnicianStatus;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianOutputDTO {

	private int technicianId;

	private int userId;

	private String specialization;

	private String status;
	private Boolean available;

	private LocalDate hireDate;
	private String city;

}