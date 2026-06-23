package com.cts.mapper;

import com.cts.dto.InvoiceDefaultersOutputDto;
import com.cts.dto.InvoiceInputDTO;
import com.cts.dto.InvoiceOutputDTO;
import com.cts.entity.Invoice;
import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;

public class InvoiceMapper {
	public static Invoice convertToInvoice(InvoiceInputDTO input,TenantProfile tenant,Lease lease) {
		return Invoice.builder()
				.tenantProfile(tenant)
				.lease(lease)
				.status(Invoice.Status.PENDING)
				.build();
	}
	public static InvoiceOutputDTO convertToInvoiceOutputDto(Invoice invoice) {
		return InvoiceOutputDTO.builder()
				.invoiceId(invoice.getInvoiceId())
				.tenantId(invoice.getTenantProfile().getTenantId())
				.leaseId(invoice.getLease().getLeaseId())
				.periodStart(invoice.getPeriodStart())
				.periodEnd(invoice.getPeriodEnd())
				.amountDue(invoice.getLease().getUnit().getRentAmount())
				.dueDate(invoice.getDueDate())
				.status(invoice.getStatus().name())
				.generatedAt(invoice.getGeneratedAt())
				.build();
	}
	
	public static InvoiceDefaultersOutputDto convertToInvoiceDefaultersOutputDto(Invoice invoice) {
        return InvoiceDefaultersOutputDto.builder()
                .invoiceId(invoice.getInvoiceId())
                .tenantId(invoice.getTenantProfile().getTenantId())
                .tenantName(invoice.getTenantProfile().getUser().getUserName())
                .leaseId(invoice.getLease().getLeaseId())
                .emailId(invoice.getTenantProfile().getUser().getEmailId())
                .phoneNo(invoice.getTenantProfile().getUser().getPhone())
                .amountDue(invoice.getLease().getUnit().getRentAmount())
                .dueDate(invoice.getDueDate())
                .build();


    }
}
