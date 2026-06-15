package com.cts.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LedgerEntryOutputDto {
	private int ledgerEntryId;
	private int invoiceId;
	private Integer accountOfficerId;
	private String accountOfficerName;
	private String unitType;
	private double amountPaid;
	private double profitPercent;
	private double profitAmount;
	private String description;
	private LocalDateTime createdAt;
}