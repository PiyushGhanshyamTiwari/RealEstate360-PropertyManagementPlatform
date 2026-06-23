package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.service.LedgerEntryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/ledger")
@Tag(description = "All operations related to ledger entries", name = "Ledger Entry Controller")
public class LedgerEntryController {

	private LedgerEntryService ledgerEntryService;

//	@Operation(summary = "Listing all ledger entries")
//	@GetMapping
//	@PreAuthorize("hasAnyRole('ACCOUNT OFFICER','ADMIN')")
//	public ResponseEntity<?> getAllLedgerEntries() {
//		List<LedgerEntryOutputDto> response = ledgerEntryService.getAllLedgerEntries();
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}

	@Operation(summary = "Fetching a ledger entry by month and year code")

    @GetMapping("/{month}/{year}")

    @PreAuthorize("hasAnyRole('ACCOUNT OFFICER','ADMIN')")

    public ResponseEntity<?> getLedgerEntryByMonthAndYear(@PathVariable int month, @PathVariable int year) {

        List<LedgerEntryOutputDto> response = ledgerEntryService.getLedgerEntryByMonthAndYear(month, year);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

//	@Operation(summary = "Fetching the ledger entry of an invoice")
//	@GetMapping("/invoice/{invoiceId}")
//	@PreAuthorize("hasAnyRole('ACCOUNT OFFICER','ADMIN')")
//	public ResponseEntity<?> getLedgerEntryByInvoiceId(@PathVariable int invoiceId) {
//		LedgerEntryOutputDto response = ledgerEntryService.getLedgerEntryByInvoiceId(invoiceId);
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}
}