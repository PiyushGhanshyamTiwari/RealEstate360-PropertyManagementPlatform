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
public class InvoiceDefaultersOutputDto {
    private int invoiceId;
    private int leaseId;
    private int tenantId;
    private String tenantName;
    private String emailId;
    private long phoneNo;
    private LocalDate dueDate;
    private double amountDue;
}