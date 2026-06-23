package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cts.dto.AccountOfficerInputDto;
import com.cts.dto.AccountOfficerOutputDto;
import com.cts.dto.LedgerEntryOutputDto;
import com.cts.service.AccountOfficerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/account-officer")
@Tag(description = "All operations related to account officers", name = "Account Officer Controller")
public class AccountOfficerController {

    private AccountOfficerService accountOfficerService;

    @Operation(summary = "Adding a new account officer")
    @PostMapping
    @PreAuthorize("hasAnyRole('ACCOUNT OFFICER','ADMIN')")
    public ResponseEntity<?> addOfficer(@RequestBody AccountOfficerInputDto input) {
        AccountOfficerOutputDto response = accountOfficerService.addOfficer(input);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @Operation(summary = "Listing all account officers")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllOfficers() {
        List<AccountOfficerOutputDto> response = accountOfficerService.getAllOfficers();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    
    @Operation(summary = "Fetching an account officer by id")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNT OFFICER')")
    public ResponseEntity<?> getOfficerById(@PathVariable int userId) {
        AccountOfficerOutputDto response = accountOfficerService.getOfficerById(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    
    @Operation(summary = "Listing ledger entries recorded by an officer")
    @GetMapping("/{userId}/ledger-entries")
    @PreAuthorize("hasAnyRole('ACCOUNT OFFICER','ADMIN')")
    public ResponseEntity<?> getOfficerLedgerEntries(@PathVariable int userId) {
        List<LedgerEntryOutputDto> response = accountOfficerService.getOfficerLedgerEntries(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}



