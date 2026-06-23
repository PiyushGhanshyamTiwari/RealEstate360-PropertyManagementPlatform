package com.cts.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaseInputDTO {
	@NotNull(message = "Unit Id should not be empty")
	private Integer unitId;
	@NotNull(message = "Tenant Id should not be empty")
	private Integer tenantId;
	
}
