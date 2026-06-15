package com.cts.service;

import java.util.List;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.Invoice;

public interface LedgerEntryService {

	// called from the invoice flow when an invoice is marked PAID
	LedgerEntryOutputDto createForPaidInvoice(Invoice invoice, Integer officerId);

	List<LedgerEntryOutputDto> getAllLedgerEntries();

	LedgerEntryOutputDto getLedgerEntryById(int ledgerEntryId);

	LedgerEntryOutputDto getLedgerEntryByInvoiceId(int invoiceId);
}