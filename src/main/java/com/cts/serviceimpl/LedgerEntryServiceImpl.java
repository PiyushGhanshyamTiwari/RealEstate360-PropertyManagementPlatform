package com.cts.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.AccountOfficer;
import com.cts.entity.Invoice;
import com.cts.entity.LedgerEntry;
import com.cts.entity.Unit;
import com.cts.mapper.LedgerEntryMapper;
import com.cts.repository.AccountOfficerRepository;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.LedgerEntryRepository;
import com.cts.service.LedgerEntryService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LedgerEntryServiceImpl implements LedgerEntryService {

	private LedgerEntryRepository ledgerEntryRepository;
	private AccountOfficerRepository accountOfficerRepository;
	private InvoiceRepository invoiceRepository;

	@Override
	public LedgerEntryOutputDto createForPaidInvoice(Invoice invoice, Integer officerId) {

		// one ledger entry per invoice (avoid duplicates if marked PAID again)
		if (ledgerEntryRepository.existsByInvoice(invoice)) {
			LedgerEntry existing = ledgerEntryRepository.findByInvoice(invoice).get();
			return LedgerEntryMapper.convertToLedgerEntryOutputDto(existing);
		}

		Unit unit = invoice.getLease().getUnit();
		String unitType = unit.getType();
		double amountPaid = unit.getRentAmount();

		double profitPercent = getProfitPercent(unitType);
		double profitAmount = (amountPaid * profitPercent) / 100;

		AccountOfficer officer = null;
		if (officerId != null) {
			officer = accountOfficerRepository.findById(officerId)
					.orElseThrow(() -> new RuntimeException("Account officer not found"));
		}

		String description = "Paid " + amountPaid + " for " + unitType
				+ ", profit " + profitPercent + "% = " + profitAmount;

		LedgerEntry entry = LedgerEntry.builder()
				.invoice(invoice)
				.accountOfficer(officer)
				.unitType(unitType)
				.amountPaid(amountPaid)
				.profitPercent(profitPercent)
				.profitAmount(profitAmount)
				.description(description)
				.build();

		LedgerEntry saved = ledgerEntryRepository.save(entry);
		return LedgerEntryMapper.convertToLedgerEntryOutputDto(saved);
	}

	// profit policy: 1BHK=8%, 2BHK=7%, 3BHK=5%, 4BHK=4%
	private double getProfitPercent(String unitType) {
		String type = unitType.trim().toUpperCase();
		switch (type) {
			case "1BHK":
				return 8;
			case "2BHK":
				return 7;
			case "3BHK":
				return 5;
			case "4BHK":
				return 4;
			default:
				throw new RuntimeException("No profit rate for unit type: " + unitType);
		}
	}

	@Override
	public List<LedgerEntryOutputDto> getAllLedgerEntries() {
		return ledgerEntryRepository.findAll().stream()
				.map(LedgerEntryMapper::convertToLedgerEntryOutputDto)
				.toList();
	}

	@Override
	public LedgerEntryOutputDto getLedgerEntryById(int ledgerEntryId) {
		LedgerEntry entry = ledgerEntryRepository.findById(ledgerEntryId)
				.orElseThrow(() -> new RuntimeException("Ledger entry not found"));
		return LedgerEntryMapper.convertToLedgerEntryOutputDto(entry);
	}

	@Override
	public LedgerEntryOutputDto getLedgerEntryByInvoiceId(int invoiceId) {
		Invoice invoice = invoiceRepository.findById(invoiceId)
				.orElseThrow(() -> new RuntimeException("Invoice Id not found"));
		LedgerEntry entry = ledgerEntryRepository.findByInvoice(invoice)
				.orElseThrow(() -> new RuntimeException("No ledger entry for this invoice"));
		return LedgerEntryMapper.convertToLedgerEntryOutputDto(entry);
	}
}