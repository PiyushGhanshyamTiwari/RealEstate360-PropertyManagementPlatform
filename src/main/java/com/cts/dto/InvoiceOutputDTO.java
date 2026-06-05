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
public class InvoiceOutputDTO {
	private int invoiceId;
	private int tenantId;
	private int leaseId;
	private LocalDate periodStart;
	private LocalDate periodEnd;
	private double amountDue;
	private LocalDate dueDate;
	private String status;
	private LocalDateTime generatedAt;
}
