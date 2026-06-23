package com.cts.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.AccountOfficer;
import com.cts.entity.Invoice;
import com.cts.entity.LedgerEntry;
import com.cts.entity.Unit;
import com.cts.exception.NoTechnicianAssignedException;
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
    @Audit(action = AuditActions.CREATE_LEDGER_ENTRY, resourceType = "LedgerEntry")
    public LedgerEntryOutputDto createForPaidInvoice(Invoice invoice, Integer officerId) {
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

    private double getProfitPercent(String unitType) {
        switch (unitType.trim().toUpperCase()) {
            case "1BHK": return 8;
            case "2BHK": return 7;
            case "3BHK": return 5;
            case "4BHK": return 4;
            default: throw new RuntimeException("No profit rate for unit type: " + unitType);
        }
    }

    @Override
    public List<LedgerEntryOutputDto> getAllLedgerEntries() {
        return ledgerEntryRepository.findAll().stream()
                .map(LedgerEntryMapper::convertToLedgerEntryOutputDto)
                .toList();
    }

    @Override
    public List<LedgerEntryOutputDto> getLedgerEntryByMonthAndYear(int month, int year) {
    	List<LedgerEntryOutputDto> list =  ledgerEntryRepository.findByMonthAndYear(month, year)

                .stream()

                .map(LedgerEntryMapper::convertToLedgerEntryOutputDto)

                .toList();

        if(list.isEmpty())

            throw new NoTechnicianAssignedException("There is no ledger entry");

        return list;
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
