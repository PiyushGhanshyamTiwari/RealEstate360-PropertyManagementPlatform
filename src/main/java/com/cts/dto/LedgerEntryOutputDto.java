package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LedgerEntryOutputDto {
    private int ledgerId;
    private int invoiceId;
    private Integer officerId;
    private String officerName;
    private String unitType;
    private double amountPaid;
    private double profitPercent;
    private double profitAmount;
    private double gstPercent;
    private double gstAmount;
    private String description;
    private LocalDateTime loggedDate;
}