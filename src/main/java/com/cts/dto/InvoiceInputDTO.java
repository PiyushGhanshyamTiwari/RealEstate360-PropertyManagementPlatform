package com.cts.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceInputDTO {
	@NotNull(message = "Tenant Id should not be empty")
	private Integer tenantId;
	@NotNull(message = "Lease Id should not be empty")
	private Integer leaseId;
}
