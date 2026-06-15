package com.cts.serviceimpl;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.AccountOfficer;
import com.cts.entity.Invoice;
import com.cts.entity.Lease;
import com.cts.entity.LedgerEntry;
import com.cts.entity.Unit;
import com.cts.mapper.LedgerEntryMapper;
import com.cts.repository.AccountOfficerRepository;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.LedgerEntryRepository;
import com.cts.serviceimpl.LedgerEntryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LedgerEntryServiceImplTest {

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private AccountOfficerRepository accountOfficerRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private LedgerEntryServiceImpl ledgerEntryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateForPaidInvoice_AlreadyExists() {
        Invoice invoice = new Invoice();
        LedgerEntry existingEntry = new LedgerEntry();
        LedgerEntryOutputDto dto = new LedgerEntryOutputDto();

        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(true);
        when(ledgerEntryRepository.findByInvoice(invoice)).thenReturn(Optional.of(existingEntry));

        try (var mocked = mockStatic(LedgerEntryMapper.class)) {
            mocked.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(existingEntry)).thenReturn(dto);

            LedgerEntryOutputDto result = ledgerEntryService.createForPaidInvoice(invoice, null);

            assertNotNull(result);
            verify(ledgerEntryRepository, times(1)).findByInvoice(invoice);
        }
    }

    @Test
    void testCreateForPaidInvoice_NewEntryWithOfficer() {
        Unit unit = new Unit();
        unit.setType("2BHK");
        unit.setRentAmount(10000.0);

        Lease lease = new Lease();
        lease.setUnit(unit);

        Invoice invoice = new Invoice();
        invoice.setLease(lease);

        AccountOfficer officer = new AccountOfficer();
        officer.setOfficerId(1);

        LedgerEntry entry = new LedgerEntry();
        LedgerEntryOutputDto dto = new LedgerEntryOutputDto();

        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);
        when(accountOfficerRepository.findById(1)).thenReturn(Optional.of(officer));
        when(ledgerEntryRepository.save(any(LedgerEntry.class))).thenReturn(entry);

        try (var mocked = mockStatic(LedgerEntryMapper.class)) {
            mocked.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(entry)).thenReturn(dto);

            LedgerEntryOutputDto result = ledgerEntryService.createForPaidInvoice(invoice, 1);

            assertNotNull(result);
            verify(ledgerEntryRepository, times(1)).save(any(LedgerEntry.class));
        }
    }

    @Test
    void testCreateForPaidInvoice_InvalidUnitType() {
        Unit unit = new Unit();
        unit.setType("Studio");
        unit.setRentAmount(5000.0);

        Lease lease = new Lease();
        lease.setUnit(unit);

        Invoice invoice = new Invoice();
        invoice.setLease(lease);

        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> ledgerEntryService.createForPaidInvoice(invoice, null));
    }

    @Test
    void testGetAllLedgerEntries() {
        LedgerEntry entry = new LedgerEntry();
        LedgerEntryOutputDto dto = new LedgerEntryOutputDto();

        when(ledgerEntryRepository.findAll()).thenReturn(Arrays.asList(entry));

        try (var mocked = mockStatic(LedgerEntryMapper.class)) {
            mocked.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(entry)).thenReturn(dto);

            var result = ledgerEntryService.getAllLedgerEntries();

            assertEquals(1, result.size());
            verify(ledgerEntryRepository, times(1)).findAll();
        }
    }

    @Test
    void testGetLedgerEntryById_Found() {
        LedgerEntry entry = new LedgerEntry();
        LedgerEntryOutputDto dto = new LedgerEntryOutputDto();

        when(ledgerEntryRepository.findById(100)).thenReturn(Optional.of(entry));

        try (var mocked = mockStatic(LedgerEntryMapper.class)) {
            mocked.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(entry)).thenReturn(dto);

            LedgerEntryOutputDto result = ledgerEntryService.getLedgerEntryById(100);

            assertNotNull(result);
            verify(ledgerEntryRepository, times(1)).findById(100);
        }
    }

    @Test
    void testGetLedgerEntryById_NotFound() {
        when(ledgerEntryRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> ledgerEntryService.getLedgerEntryById(999));
    }

    @Test
    void testGetLedgerEntryByInvoiceId_Found() {
        Invoice invoice = new Invoice();
        LedgerEntry entry = new LedgerEntry();
        LedgerEntryOutputDto dto = new LedgerEntryOutputDto();

        when(invoiceRepository.findById(10)).thenReturn(Optional.of(invoice));
        when(ledgerEntryRepository.findByInvoice(invoice)).thenReturn(Optional.of(entry));

        try (var mocked = mockStatic(LedgerEntryMapper.class)) {
            mocked.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(entry)).thenReturn(dto);

            LedgerEntryOutputDto result = ledgerEntryService.getLedgerEntryByInvoiceId(10);

            assertNotNull(result);
            verify(invoiceRepository, times(1)).findById(10);
            verify(ledgerEntryRepository, times(1)).findByInvoice(invoice);
        }
    }

    @Test
    void testGetLedgerEntryByInvoiceId_NotFoundInvoice() {
        when(invoiceRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> ledgerEntryService.getLedgerEntryByInvoiceId(99));
    }

    @Test
    void testGetLedgerEntryByInvoiceId_NoEntry() {
        Invoice invoice = new Invoice();
        when(invoiceRepository.findById(10)).thenReturn(Optional.of(invoice));
        when(ledgerEntryRepository.findByInvoice(invoice)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> ledgerEntryService.getLedgerEntryByInvoiceId(10));
    }
}
