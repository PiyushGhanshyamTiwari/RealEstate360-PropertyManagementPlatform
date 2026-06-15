package com.cts.serviceimpl;

import com.cts.dto.AccountOfficerInputDto;
import com.cts.dto.AccountOfficerOutputDto;
import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.AccountOfficer;
import com.cts.entity.Invoice;
import com.cts.entity.LedgerEntry;
import com.cts.mapper.AccountOfficerMapper;
import com.cts.mapper.LedgerEntryMapper;
import com.cts.repository.AccountOfficerRepository;
import com.cts.repository.LedgerEntryRepository;
import com.cts.serviceimpl.AccountOfficerServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountOfficerServiceImplTest {

    @Mock
    private AccountOfficerRepository accountOfficerRepository;

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @InjectMocks
    private AccountOfficerServiceImpl accountOfficerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOfficer() {
        AccountOfficerInputDto inputDto = new AccountOfficerInputDto();
        inputDto.setFullName("John Doe");

        AccountOfficer officer = AccountOfficerMapper.convertToAccountOfficer(inputDto);
        AccountOfficer savedOfficer = new AccountOfficer();
        savedOfficer.setOfficerId(1);
        savedOfficer.setFullName("John Doe");

        when(accountOfficerRepository.save(any(AccountOfficer.class))).thenReturn(savedOfficer);

        AccountOfficerOutputDto result = accountOfficerService.addOfficer(inputDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        verify(accountOfficerRepository, times(1)).save(any(AccountOfficer.class));
    }

    @Test
    void testGetAllOfficers() {
        AccountOfficer officer1 = new AccountOfficer();
        officer1.setOfficerId(1);
        officer1.setFullName("John Doe");

        AccountOfficer officer2 = new AccountOfficer();
        officer2.setOfficerId(2);
        officer2.setFullName("Jane Smith");

        when(accountOfficerRepository.findAll()).thenReturn(Arrays.asList(officer1, officer2));

        List<AccountOfficerOutputDto> result = accountOfficerService.getAllOfficers();

        assertEquals(2, result.size());
        verify(accountOfficerRepository, times(1)).findAll();
    }

    @Test
    void testGetOfficerById_Found() {
        AccountOfficer officer = new AccountOfficer();
        officer.setOfficerId(1);
        officer.setFullName("John Doe");

        when(accountOfficerRepository.findById(1)).thenReturn(Optional.of(officer));

        AccountOfficerOutputDto result = accountOfficerService.getOfficerById(1);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        verify(accountOfficerRepository, times(1)).findById(1);
    }

    @Test
    void testGetOfficerById_NotFound() {
        when(accountOfficerRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountOfficerService.getOfficerById(99));

        assertEquals("Account officer not found", exception.getMessage());
    }

    @Test
    void testGetOfficerLedgerEntries() {
        AccountOfficer officer = new AccountOfficer();
        officer.setOfficerId(1);
        officer.setFullName("John Doe");

        // LedgerEntryMapper.convertToLedgerEntryOutputDto reads
        // entry.getInvoice().getInvoiceId(), so the entry must carry a non-null
        // Invoice (and officer) to be mapped without a NullPointerException.
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(500);

        LedgerEntry entry = new LedgerEntry();
        entry.setLedgerEntryId(100);
        entry.setInvoice(invoice);
        entry.setAccountOfficer(officer);

        when(accountOfficerRepository.findById(1)).thenReturn(Optional.of(officer));
        when(ledgerEntryRepository.findByAccountOfficer(officer)).thenReturn(Arrays.asList(entry));

        List<LedgerEntryOutputDto> result = accountOfficerService.getOfficerLedgerEntries(1);

        assertEquals(1, result.size());
        verify(ledgerEntryRepository, times(1)).findByAccountOfficer(officer);
    }
}
