package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.*;
import com.cts.exception.NoTechnicianAssignedException;
import com.cts.mapper.LedgerEntryMapper;
import com.cts.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LedgerEntryServiceImplTest {

    @Mock private LedgerEntryRepository ledgerEntryRepository;
    @Mock private AccountOfficerRepository accountOfficerRepository;
    @Mock private InvoiceRepository invoiceRepository;

    @InjectMocks
    private LedgerEntryServiceImpl service;

    private Invoice invoice;
    private Lease lease;
    private Unit unit;
    private LedgerEntry entry;
    private AccountOfficer officer;

    @BeforeEach
    void setup() {
        unit = new Unit();
        unit.setType("2BHK");
        unit.setRentAmount(10000);

        lease = new Lease();
        lease.setUnit(unit);

        invoice = new Invoice();
        invoice.setLease(lease);

        officer = new AccountOfficer();
        officer.setOfficerId(1);

        entry = new LedgerEntry();
        entry.setInvoice(invoice);
    }

    
    @Test
    void testCreateForPaidInvoiceAlreadyExists() {
        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(true);
        when(ledgerEntryRepository.findByInvoice(invoice)).thenReturn(Optional.of(entry));

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {

            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(entry))
                    .thenReturn(new LedgerEntryOutputDto());

            LedgerEntryOutputDto result =
                    service.createForPaidInvoice(invoice, 1);

            assertNotNull(result);
        }
    }

    
    @Test
    void testCreateForPaidInvoiceSuccessWithOfficer() {
        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);
        when(accountOfficerRepository.findById(1)).thenReturn(Optional.of(officer));
        when(ledgerEntryRepository.save(any())).thenReturn(entry);

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {

            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(any()))
                    .thenReturn(new LedgerEntryOutputDto());

            LedgerEntryOutputDto result =
                    service.createForPaidInvoice(invoice, 1);

            assertNotNull(result);
        }
    }

    
    @Test
    void testCreateForPaidInvoiceWithoutOfficer() {
        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);
        when(ledgerEntryRepository.save(any())).thenReturn(entry);

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {

            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(any()))
                    .thenReturn(new LedgerEntryOutputDto());

            LedgerEntryOutputDto result =
                    service.createForPaidInvoice(invoice, null);

            assertNotNull(result);
        }
    }

     
    @Test
    void testCreateForPaidInvoiceOfficerNotFound() {
        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);
        when(accountOfficerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.createForPaidInvoice(invoice, 1));
    }

     
    @Test
    void testProfitPercent1BHK() {
        unit.setType("1BHK");

        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);
        when(ledgerEntryRepository.save(any())).thenReturn(entry);

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {
            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(any()))
                    .thenReturn(new LedgerEntryOutputDto());

            service.createForPaidInvoice(invoice, null);
        }
    }

    @Test
    void testProfitPercent3BHK() {
        unit.setType("3BHK");

        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);
        when(ledgerEntryRepository.save(any())).thenReturn(entry);

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {
            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(any()))
                    .thenReturn(new LedgerEntryOutputDto());

            assertNotNull(service.createForPaidInvoice(invoice, null));
        }
    }

    @Test
    void testProfitPercent4BHK() {
        unit.setType("4BHK");

        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);
        when(ledgerEntryRepository.save(any())).thenReturn(entry);

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {
            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(any()))
                    .thenReturn(new LedgerEntryOutputDto());

            assertNotNull(service.createForPaidInvoice(invoice, null));
        }
    }

     
    @Test
    void testCreateForPaidInvoiceInvalidUnitType() {
        unit.setType("UNKNOWN");

        when(ledgerEntryRepository.existsByInvoice(invoice)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> service.createForPaidInvoice(invoice, null));
    }

    
    @Test
    void testGetAllLedgerEntries() {
        when(ledgerEntryRepository.findAll()).thenReturn(List.of(entry));

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {
            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(entry))
                    .thenReturn(new LedgerEntryOutputDto());

            List<LedgerEntryOutputDto> result = service.getAllLedgerEntries();

            assertEquals(1, result.size());
        }
    }

     
    @Test
    void testGetLedgerEntryByMonthAndYearSuccess() {
        when(ledgerEntryRepository.findByMonthAndYear(1, 2024))
                .thenReturn(List.of(entry));

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {
            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(entry))
                    .thenReturn(new LedgerEntryOutputDto());

            List<LedgerEntryOutputDto> result =
                    service.getLedgerEntryByMonthAndYear(1, 2024);

            assertEquals(1, result.size());
        }
    }

     
    @Test
    void testGetLedgerEntryByMonthAndYearEmpty() {
        when(ledgerEntryRepository.findByMonthAndYear(1, 2024))
                .thenReturn(Collections.emptyList());

        assertThrows(NoTechnicianAssignedException.class,
                () -> service.getLedgerEntryByMonthAndYear(1, 2024));
    }

    
    @Test
    void testGetLedgerEntryByInvoiceIdSuccess() {
        when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
        when(ledgerEntryRepository.findByInvoice(invoice)).thenReturn(Optional.of(entry));

        try (MockedStatic<LedgerEntryMapper> mapper = mockStatic(LedgerEntryMapper.class)) {
            mapper.when(() -> LedgerEntryMapper.convertToLedgerEntryOutputDto(entry))
                    .thenReturn(new LedgerEntryOutputDto());

            LedgerEntryOutputDto result =
                    service.getLedgerEntryByInvoiceId(1);

            assertNotNull(result);
        }
    }

  
    @Test
    void testGetLedgerEntryByInvoiceIdInvoiceNotFound() {
        when(invoiceRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getLedgerEntryByInvoiceId(1));
    }

     
    @Test
    void testGetLedgerEntryByInvoiceIdLedgerNotFound() {
        when(invoiceRepository.findById(1)).thenReturn(Optional.of(invoice));
        when(ledgerEntryRepository.findByInvoice(invoice)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getLedgerEntryByInvoiceId(1));
    }
}