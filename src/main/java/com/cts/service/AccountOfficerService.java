package com.cts.service;

import java.util.List;

import com.cts.dto.AccountOfficerInputDto;
import com.cts.dto.AccountOfficerOutputDto;
import com.cts.dto.LedgerEntryOutputDto;

public interface AccountOfficerService {

	AccountOfficerOutputDto addOfficer(AccountOfficerInputDto input);

	List<AccountOfficerOutputDto> getAllOfficers();

	AccountOfficerOutputDto getOfficerById(int officerId);

	// ledger entries this officer recorded (officer linked to invoice + ledger)
	List<LedgerEntryOutputDto> getOfficerLedgerEntries(int officerId);
}