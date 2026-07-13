package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cts.dto.InvoiceDefaultersOutputDto;
import com.cts.dto.InvoiceOutputDTO;
import com.cts.entity.Invoice;
import com.cts.entity.Lease;
import com.cts.mapper.InvoiceMapper;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.LeaseRepository;
import com.cts.service.LedgerEntryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private LeaseRepository leaseRepository;

    @Mock
    private LedgerEntryService ledgerEntryService;

    @InjectMocks
    private InvoiceServiceImpl service;

    private Lease lease;
    private Invoice invoice;

    @BeforeEach
    void setup() {
        lease = new Lease();
        lease.setLeaseId(1);

        invoice = new Invoice();
        invoice.setInvoiceId(100);
        invoice.setLease(lease);
    }

    
    @Test
    void testListInvoiceWithLeaseIdSuccess() {
        when(leaseRepository.findById(1)).thenReturn(Optional.of(lease));
        when(invoiceRepository.findByLease(lease)).thenReturn(List.of(invoice));

        try (MockedStatic<InvoiceMapper> mapper = mockStatic(InvoiceMapper.class)) {

            InvoiceOutputDTO dto = new InvoiceOutputDTO();

            mapper.when(() -> InvoiceMapper.convertToInvoiceOutputDto(invoice))
                    .thenReturn(dto);

            List<InvoiceOutputDTO> result = service.listInvoiceWithLeaseId(1);

            assertEquals(1, result.size());
            verify(leaseRepository).findById(1);
            verify(invoiceRepository).findByLease(lease);
        }
    }

    
    @Test
    void testListInvoiceWithLeaseIdLeaseNotFound() {
        when(leaseRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.listInvoiceWithLeaseId(1));

        assertEquals("Lease Id not exists", ex.getMessage());
    }

    
    @Test
    void testUpdateStatusPaid() {
        invoice.setStatus(Invoice.Status.PENDING);

        when(invoiceRepository.findById(100)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any())).thenReturn(invoice);

        try (MockedStatic<InvoiceMapper> mapper = mockStatic(InvoiceMapper.class)) {

            mapper.when(() -> InvoiceMapper.convertToInvoiceOutputDto(invoice))
                    .thenReturn(new InvoiceOutputDTO());

            InvoiceOutputDTO result =
                    service.updateStatus(100, "PAID", 10);

            assertNotNull(result);
            verify(ledgerEntryService).createForPaidInvoice(invoice, 10);
        }
    }

   
    @Test
    void testUpdateStatusNotPaid() {
        when(invoiceRepository.findById(100)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any())).thenReturn(invoice);

        try (MockedStatic<InvoiceMapper> mapper = mockStatic(InvoiceMapper.class)) {

            mapper.when(() -> InvoiceMapper.convertToInvoiceOutputDto(invoice))
                    .thenReturn(new InvoiceOutputDTO());

            service.updateStatus(100, "PENDING", 10);

            verify(ledgerEntryService, never()).createForPaidInvoice(any(), any());
        }
    }

    
    @Test
    void testUpdateStatusInvoiceNotFound() {
        when(invoiceRepository.findById(100)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateStatus(100, "PAID", 10));

        assertEquals("Invoice Id not found", ex.getMessage());
    }

    
    @Test
    void testGetDefaulters() {
        when(invoiceRepository.findDefaulters()).thenReturn(List.of(invoice));

        try (MockedStatic<InvoiceMapper> mapper = mockStatic(InvoiceMapper.class)) {

            InvoiceDefaultersOutputDto dto = new InvoiceDefaultersOutputDto();

            mapper.when(() -> InvoiceMapper.convertToInvoiceDefaultersOutputDto(invoice))
                    .thenReturn(dto);

            List<InvoiceDefaultersOutputDto> result = service.getDefaulters();

            assertEquals(1, result.size());
        }
    }

   
    @Test
    void testGetDefaultersEmpty() {
        when(invoiceRepository.findDefaulters()).thenReturn(Collections.emptyList());

        List<InvoiceDefaultersOutputDto> result = service.getDefaulters();

        assertTrue(result.isEmpty());
    }
}