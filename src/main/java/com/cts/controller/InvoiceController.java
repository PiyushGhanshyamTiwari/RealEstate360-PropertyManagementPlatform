package com.cts.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.InvoiceDefaultersOutputDto;
import com.cts.dto.InvoiceOutputDTO;
import com.cts.service.InvoiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/invoice")
@Tag(description = "All operations related to invoice", name = "Invoice Controller")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(summary = "Listing the invoice details wrt leaseId")
    @GetMapping("/leaseId/{leaseId}")
    @PreAuthorize("hasAnyRole('ACCOUNT OFFICER','TENANT','OWNER')")
    public ResponseEntity<List<InvoiceOutputDTO>> listInvoiceWithLeaseId(@PathVariable int leaseId) {
        List<InvoiceOutputDTO> response = invoiceService.listInvoiceWithLeaseId(leaseId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Updating the invoice status wrt InvoiceId")
    @PutMapping("/{invoiceId}/{status}")
    @PreAuthorize("hasRole('ACCOUNT OFFICER')")
    public ResponseEntity<InvoiceOutputDTO> updateStatus(
            @PathVariable int invoiceId,
            @PathVariable String status,
            @RequestParam Integer officerId) {
        InvoiceOutputDTO response = invoiceService.updateStatus(invoiceId, status, officerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Listing defaulter invoices (pending or overdue, due on or before today)")
    @GetMapping("/defaulters")
    @PreAuthorize("hasAnyRole('ACCOUNT OFFICER','ADMIN')")
    public ResponseEntity<List<InvoiceDefaultersOutputDto>> getDefaulters() {
        List<InvoiceDefaultersOutputDto> response = invoiceService.getDefaulters();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}