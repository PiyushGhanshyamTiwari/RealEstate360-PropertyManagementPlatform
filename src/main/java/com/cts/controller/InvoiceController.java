package com.cts.controller;

import java.net.http.WebSocket.Listener;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.dto.InvoiceInputDTO;
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
	private InvoiceService invoiceService;
	@Operation(summary = "Generating the invoice")
	@PostMapping("/generate")
	@PreAuthorize("hasRole('ACCOUNT OFFICER')")
	public ResponseEntity<?> generateInvoice(@RequestBody InvoiceInputDTO input){
		List<InvoiceOutputDTO> response = invoiceService.generateInvoice(input);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@Operation(summary = "Listing the invoice details wrt leaseId")
	@GetMapping("/leaseId/{leaseId}")
	@PreAuthorize("hasRole('ACCOUNT OFFICER')")
	public ResponseEntity<?> listInvoiceWithLeaseId(@PathVariable int leaseId){
		List<InvoiceOutputDTO> response = invoiceService.listInvoiceWithLeaseId(leaseId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Operation(summary = "Updating the invoice status wrt InvoiceId")
	@PutMapping("/{invoiceId}/{status}/{officerId}")
	@PreAuthorize("hasRole('ACCOUNT OFFICER')")
	public ResponseEntity<?> updateStatus(@PathVariable int invoiceId,@PathVariable String status,@RequestParam Integer officerId){
		InvoiceOutputDTO response = invoiceService.updateStatus(invoiceId,status,officerId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}