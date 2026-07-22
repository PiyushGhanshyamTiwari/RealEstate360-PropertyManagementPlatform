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
        double baseRent = unit.getRentAmount();

        // 1. Calculate Profit
        double profitPercent = getProfitPercent(unitType);
        double profitAmount = (baseRent * profitPercent) / 100;

        // 2. Calculate GST Taxation (No zero values used)
        double gstPercent = getGstPercent(unitType);
        double gstAmount = (baseRent * gstPercent) / 100;

        // 3. Total payable amount including GST
        double totalAmountPaid = baseRent + gstAmount;

        AccountOfficer officer = null;
        if (officerId != null) {
            officer = accountOfficerRepository.findById(officerId)
                    .orElseThrow(() -> new RuntimeException("Account officer not found"));
        }

        // Description string formatted without zero literals
        String description = String.format(
                "Base Rent: %.2f, Unit: %s | GST (%.1f%%): %.2f | Profit (%.1f%%): %.2f | Total Paid: %.2f",
                baseRent, unitType, gstPercent, gstAmount, profitPercent, profitAmount, totalAmountPaid
        );

        LedgerEntry entry = LedgerEntry.builder()
                .invoice(invoice)
                .accountOfficer(officer)
                .unitType(unitType)
                .amountPaid(totalAmountPaid)
                .profitPercent(profitPercent)
                .profitAmount(profitAmount)
                .description(description)
                .build();

        LedgerEntry saved = ledgerEntryRepository.save(entry);
        return LedgerEntryMapper.convertToLedgerEntryOutputDto(saved);
    }

    private double getProfitPercent(String unitType) {
        switch (unitType.trim().toUpperCase()) {
            case "APARTMENT": return 8;
            case "VILLA": return 10;
            case "OFFICE": return 9;
            case "STUDIO": return 12;
            case "COMMERCIAL": return 11;
            default: throw new RuntimeException("No profit rate for unit type: " + unitType);
        }
    }

    /**
     * Returns applicable non-zero GST percentage based on unit type.
     */
    private double getGstPercent(String unitType) {
        switch (unitType.trim().toUpperCase()) {
            case "APARTMENT":
                return 5.0;  // Standard low rate GST
            case "STUDIO":
                return 5.0; // Mid tier GST rate
            case "VILLA":
            case "OFFICE":
            case "COMMERCIAL":
                return 12.0; // Standard commercial / luxury GST rate
            default:
                throw new RuntimeException("No GST taxation rate defined for unit type: " + unitType);
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
        List<LedgerEntryOutputDto> list = ledgerEntryRepository.findByMonthAndYear(month, year)
                .stream()
                .map(LedgerEntryMapper::convertToLedgerEntryOutputDto)
                .toList();

        if (list.isEmpty()) {
            throw new NoTechnicianAssignedException("There is no ledger entry");
        }

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