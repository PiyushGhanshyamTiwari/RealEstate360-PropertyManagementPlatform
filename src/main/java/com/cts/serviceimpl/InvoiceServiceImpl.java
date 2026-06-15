package com.cts.serviceimpl;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.stereotype.Service;
 
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
    public List<InvoiceOutputDTO> generateInvoice(InvoiceInputDTO input) {
 
        TenantProfile tenant = tenantProfileRepository.findById(input.getTenantId())
                .orElseThrow(() -> new TenantIdNotFoundException("Tenant not found"));
 
        Lease lease = leaseRepository.findById(input.getLeaseId())
                .orElseThrow(() -> new RuntimeException("Lease not found"));
 
        LocalDate leaseStart = lease.getStartDate();
        LocalDate leaseEnd = lease.getEndDate();
 
        LocalDate currentMonth = leaseStart.withDayOfMonth(1);
 
        List<Invoice> invoices = new ArrayList<>();
 
        while (!currentMonth.isAfter(leaseEnd)) {
 
            Invoice invoice = InvoiceMapper.convertToInvoice(input, tenant, lease);
 
            LocalDate periodStart = currentMonth;
            LocalDate periodEnd = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth());
 
            if (periodEnd.isAfter(leaseEnd)) {
                periodEnd = leaseEnd;
            }
 
            LocalDate dueDate = currentMonth.withDayOfMonth(5);
 
            invoice.setPeriodStart(periodStart);
            invoice.setPeriodEnd(periodEnd);
            invoice.setDueDate(dueDate);
 
            invoices.add(invoice);
 
            currentMonth = currentMonth.plusMonths(1);
        }
 
        invoiceRepository.saveAll(invoices);
 
        return invoices.stream()
                .map(InvoiceMapper::convertToInvoiceOutputDto)
                .toList();
    }
 
	@Override
	public List<InvoiceOutputDTO> listInvoiceWithLeaseId(int leaseId) {
		// TODO Auto-generated method stub
		Lease lease = leaseRepository.findById(leaseId)
				.orElseThrow(()->new RuntimeException("Lease Id not exists"));
		List<Invoice> invoice = invoiceRepository.findByLease(lease);

 
		return invoice.stream()
				.map(InvoiceMapper::convertToInvoiceOutputDto)
				.toList();
	}
 
	@Override
	public InvoiceOutputDTO updateStatus(int invoiceId, String status, Integer officerId) {
		Invoice invoice = invoiceRepository.findById(invoiceId)
				.orElseThrow(()->new RuntimeException("Invoice Id not found"));
		Invoice.Status enumStatus = Invoice.Status.valueOf(status);
		invoice.setStatus(enumStatus);
		Invoice updatedInvoice = invoiceRepository.save(invoice);
 
		if (enumStatus == Invoice.Status.PAID) {
			ledgerEntryService.createForPaidInvoice(updatedInvoice, officerId);
		}
 
		return InvoiceMapper.convertToInvoiceOutputDto(updatedInvoice);
	}
}