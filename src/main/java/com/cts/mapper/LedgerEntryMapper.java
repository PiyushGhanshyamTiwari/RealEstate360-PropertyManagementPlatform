package com.cts.mapper;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.AccountOfficer;
import com.cts.entity.LedgerEntry;

public class LedgerEntryMapper {

	public static LedgerEntryOutputDto convertToLedgerEntryOutputDto(LedgerEntry entry) {
		AccountOfficer officer = entry.getAccountOfficer();
		return LedgerEntryOutputDto.builder()
				.ledgerEntryId(entry.getLedgerEntryId())
				.invoiceId(entry.getInvoice().getInvoiceId())
				.accountOfficerId(officer == null ? null : officer.getOfficerId())
				.accountOfficerName(officer == null ? null : officer.getFullName())
				.unitType(entry.getUnitType())
				.amountPaid(entry.getAmountPaid())
				.profitPercent(entry.getProfitPercent())
				.profitAmount(entry.getProfitAmount())
				.description(entry.getDescription())
				.createdAt(entry.getCreatedAt())
				.build();
	}
}