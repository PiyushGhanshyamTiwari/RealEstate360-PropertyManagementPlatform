package com.cts.serviceimpl;
 
import com.cts.dto.InvoiceInputDTO;
import com.cts.dto.InvoiceOutputDTO;
import com.cts.dto.LedgerEntryOutputDto;
import com.cts.entity.Invoice;
import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.mapper.InvoiceMapper;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.LeaseRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.service.LedgerEntryService;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
 
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
public class InvoiceServiceImplTest {
 
    @Mock
    private InvoiceRepository invoiceRepository;
 
    @Mock
    private TenantProfileRepository tenantProfileRepository;
 
    @Mock
    private LeaseRepository leaseRepository;
 
    @Mock                                          // FIX 1: was missing — InvoiceServiceImpl now
    private LedgerEntryService ledgerEntryService; // injects this; without it updateStatus("PAID")
                                                   // throws NPE when createForPaidInvoice is called
 
    @InjectMocks
    private InvoiceServiceImpl invoiceService;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
 
    @Test
    void testGenerateInvoice_Success() {
        InvoiceInputDTO inputDto = new InvoiceInputDTO();
        inputDto.setTenantId(1);
        inputDto.setLeaseId(10);
 
        TenantProfile tenant = new TenantProfile();
        tenant.setTenantId(1);
 
        Lease lease = new Lease();
        lease.setLeaseId(10);
        lease.setStartDate(LocalDate.of(2024, 1, 1));
        lease.setEndDate(LocalDate.of(2024, 3, 31));
 
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(100);
 
        InvoiceOutputDTO outputDto = new InvoiceOutputDTO();
        outputDto.setInvoiceId(100);
 
        when(tenantProfileRepository.findById(1)).thenReturn(Optional.of(tenant));
        when(leaseRepository.findById(10)).thenReturn(Optional.of(lease));
 
        try (MockedStatic<InvoiceMapper> mockedMapper = mockStatic(InvoiceMapper.class)) {
            mockedMapper.when(() -> InvoiceMapper.convertToInvoice(inputDto, tenant, lease))
                    .thenReturn(invoice);
            mockedMapper.when(() -> InvoiceMapper.convertToInvoiceOutputDto(invoice))
                    .thenReturn(outputDto);
 
            List<InvoiceOutputDTO> result = invoiceService.generateInvoice(inputDto);
 
            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(invoiceRepository, times(1)).saveAll(anyList());
        }
    }
 
    @Test
    void testGenerateInvoice_TenantNotFound() {
        InvoiceInputDTO inputDto = new InvoiceInputDTO();
        inputDto.setTenantId(99);
        inputDto.setLeaseId(10);
 
        when(tenantProfileRepository.findById(99)).thenReturn(Optional.empty());
 
        assertThrows(TenantIdNotFoundException.class,
                () -> invoiceService.generateInvoice(inputDto));
    }
 
    @Test
    void testGenerateInvoice_LeaseNotFound() {
        InvoiceInputDTO inputDto = new InvoiceInputDTO();
        inputDto.setTenantId(1);
        inputDto.setLeaseId(99);
 
        TenantProfile tenant = new TenantProfile();
        tenant.setTenantId(1);
 
        when(tenantProfileRepository.findById(1)).thenReturn(Optional.of(tenant));
        when(leaseRepository.findById(99)).thenReturn(Optional.empty());
 
        assertThrows(RuntimeException.class,
                () -> invoiceService.generateInvoice(inputDto));
    }
 
    @Test
    void testListInvoiceWithLeaseId() {
        Lease lease = new Lease();
        lease.setLeaseId(10);
 
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(100);
 
        InvoiceOutputDTO dto = new InvoiceOutputDTO();
        dto.setInvoiceId(100);
 
        when(leaseRepository.findById(10)).thenReturn(Optional.of(lease));
        when(invoiceRepository.findByLease(lease)).thenReturn(Arrays.asList(invoice));
 
        try (MockedStatic<InvoiceMapper> mockedMapper = mockStatic(InvoiceMapper.class)) {
            mockedMapper.when(() -> InvoiceMapper.convertToInvoiceOutputDto(invoice)).thenReturn(dto);
 
            List<InvoiceOutputDTO> result = invoiceService.listInvoiceWithLeaseId(10);
 
            assertEquals(1, result.size());
            assertEquals(100, result.get(0).getInvoiceId());
            verify(invoiceRepository, times(1)).findByLease(lease);
        }
    }
 
    @Test
    void testUpdateStatus_Success() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(100);
        invoice.setStatus(Invoice.Status.PENDING);
 
        Invoice updatedInvoice = new Invoice();
        updatedInvoice.setInvoiceId(100);
        updatedInvoice.setStatus(Invoice.Status.PAID);
 
        InvoiceOutputDTO dto = new InvoiceOutputDTO();
        dto.setInvoiceId(100);
        dto.setStatus("PAID");
 
        when(invoiceRepository.findById(100)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(updatedInvoice);
 
        // FIX 2: stub createForPaidInvoice — status is PAID so the service
        // now calls this; without the stub the mock returns null (safe) but
        // we should also verify it actually fired
        when(ledgerEntryService.createForPaidInvoice(updatedInvoice, null))
                .thenReturn(new LedgerEntryOutputDto());
 
        try (MockedStatic<InvoiceMapper> mockedMapper = mockStatic(InvoiceMapper.class)) {
            mockedMapper.when(() -> InvoiceMapper.convertToInvoiceOutputDto(updatedInvoice)).thenReturn(dto);
 
            InvoiceOutputDTO result = invoiceService.updateStatus(100, "PAID", null);
 
            assertEquals("PAID", result.getStatus());
            verify(invoiceRepository, times(1)).save(invoice);
            verify(ledgerEntryService, times(1)).createForPaidInvoice(updatedInvoice, null);
        }
    }
 
    @Test
    void testUpdateStatus_NotFound() {
        when(invoiceRepository.findById(999)).thenReturn(Optional.empty());
 
        // FIX 3: was updateStatus(999, "PAID") — 2-arg, compile error.
        // Signature is now updateStatus(int, String, Integer)
        assertThrows(RuntimeException.class,
                () -> invoiceService.updateStatus(999, "PAID", null));
    }
}