package com.cts.mapper;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.LedgerEntry;

public class LedgerEntryMapper {

    public static LedgerEntryOutputDto convertToLedgerEntryOutputDto(LedgerEntry entry) {
        if (entry == null) {
            return null;
        }

        double totalPaid = entry.getAmountPaid(); // e.g. 56000.0
        double gstPercent = getGstPercent(entry.getUnitType()); // e.g. 12.0

        // Extract base rent (50000.0)
        double baseRent = totalPaid / (1 + (gstPercent / 100.0));

        // Calculate GST based on base rent (6000.0)
        double gstAmount = baseRent * (gstPercent / 100.0);

        return LedgerEntryOutputDto.builder()
                .ledgerId(entry.getLedgerEntryId())
                .invoiceId(entry.getInvoice() != null ? entry.getInvoice().getInvoiceId() : null)
                .officerId(entry.getAccountOfficer() != null ? entry.getAccountOfficer().getOfficerId() : null)
                .officerName(entry.getAccountOfficer() != null ? entry.getAccountOfficer().getFullName() : "System Automated")
                .unitType(entry.getUnitType())
                .amountPaid(entry.getAmountPaid())
                .profitPercent(entry.getProfitPercent())
                .profitAmount(entry.getProfitAmount())
                .gstPercent(gstPercent)
                .gstAmount(gstAmount) // Calculates 6000.00
                .description(entry.getDescription())
                .loggedDate(entry.getCreatedAt())
                .build();
    }

    /**
     * Returns applicable non-zero GST percentage based on unit type.
     */
    private static double getGstPercent(String unitType) {
        if (unitType == null) {
            return 0.0;
        }

        switch (unitType.trim().toUpperCase()) {
            case "APARTMENT":
            case "STUDIO":
                return 5.0; // Low/Mid tier GST rate
            case "VILLA":
            case "OFFICE":
            case "COMMERCIAL":
                return 12.0; // Standard commercial / luxury GST rate
            default:
                return 0.0;
        }
    }
}