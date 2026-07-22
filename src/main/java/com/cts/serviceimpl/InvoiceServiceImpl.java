package com.cts.serviceimpl;

import java.time.LocalDate;
import java.time.LocalDateTime; // Added for timestamp update
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.InvoiceDefaultersOutputDto;
import com.cts.dto.InvoiceInputDTO;
import com.cts.dto.InvoiceOutputDTO;
import com.cts.entity.Invoice;
import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.mapper.InvoiceMapper;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.LeaseRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.service.InvoiceService;
import com.cts.service.LedgerEntryService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private InvoiceRepository invoiceRepository;
    private TenantProfileRepository tenantProfileRepository;
    private LeaseRepository leaseRepository;
    private LedgerEntryService ledgerEntryService;

    @Override
    public List<InvoiceOutputDTO> listInvoiceWithLeaseId(int leaseId) {
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease Id not exists"));
        List<Invoice> invoice = invoiceRepository.findByLease(lease);
        return invoice.stream()
                .map(InvoiceMapper::convertToInvoiceOutputDto)
                .toList();
    }

    @Override
    @Audit(action = AuditActions.UPDATE_INVOICE, resourceType = "Invoice")
    public InvoiceOutputDTO updateStatus(int invoiceId, String status, Integer officerId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice Id not found"));

        Invoice.Status enumStatus = Invoice.Status.valueOf(status.toUpperCase());
        invoice.setStatus(enumStatus);

        // Update the timestamp in the database when payment is recorded
        if (enumStatus == Invoice.Status.PAID) {
            invoice.setGeneratedAt(LocalDateTime.now());
            // Note: If your Invoice entity uses a different timestamp field name
            // like setPaidAt(...) or setUpdatedAt(...), adjust this setter name accordingly.
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        if (enumStatus == Invoice.Status.PAID) {
            ledgerEntryService.createForPaidInvoice(updatedInvoice, officerId);
        }

        return InvoiceMapper.convertToInvoiceOutputDto(updatedInvoice);
    }

    @Override
    public List<InvoiceDefaultersOutputDto> getDefaulters() {
        List<Invoice> invoices = invoiceRepository.findDefaulters();
        return invoices.stream()
                .map(InvoiceMapper::convertToInvoiceDefaultersOutputDto)
                .toList();
    }
}