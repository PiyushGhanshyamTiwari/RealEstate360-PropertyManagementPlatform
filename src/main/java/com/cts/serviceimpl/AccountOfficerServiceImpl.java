package com.cts.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.AccountOfficerInputDto;
import com.cts.dto.AccountOfficerOutputDto;
import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.AccountOfficer;
import com.cts.entity.User;
import com.cts.mapper.AccountOfficerMapper;
import com.cts.mapper.LedgerEntryMapper;
import com.cts.repository.AccountOfficerRepository;
import com.cts.repository.LedgerEntryRepository;
import com.cts.repository.UserRepository;
import com.cts.service.AccountOfficerService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountOfficerServiceImpl implements AccountOfficerService {

    private AccountOfficerRepository accountOfficerRepository;
    private LedgerEntryRepository ledgerEntryRepository;
    private UserRepository userRepository;
    
    @Override
    @Audit(action = AuditActions.CREATE_ACCOUNT_OFFICER, resourceType = "AccountOfficer")
    public AccountOfficerOutputDto addOfficer(AccountOfficerInputDto input) {
    	User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        AccountOfficer officer = AccountOfficerMapper.convertToAccountOfficer(input, user);
        AccountOfficer saved = accountOfficerRepository.save(officer);
        return AccountOfficerMapper.convertToAccountOfficerOutputDto(saved);
    }

    @Override
    public List<AccountOfficerOutputDto> getAllOfficers() {
        return accountOfficerRepository.findAll().stream()
                .map(AccountOfficerMapper::convertToAccountOfficerOutputDto)
                .toList();
    }

    @Override
    public AccountOfficerOutputDto getOfficerById(int officerId) {
        AccountOfficer officer = accountOfficerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Account officer not found"));
        return AccountOfficerMapper.convertToAccountOfficerOutputDto(officer);
    }

    @Override
    public List<LedgerEntryOutputDto> getOfficerLedgerEntries(int officerId) {
        AccountOfficer officer = accountOfficerRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Account officer not found"));
        return ledgerEntryRepository.findByAccountOfficer(officer).stream()
                .map(LedgerEntryMapper::convertToLedgerEntryOutputDto)
                .toList();
    }
}
