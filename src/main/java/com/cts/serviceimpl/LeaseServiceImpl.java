package com.cts.serviceimpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.InvoiceInputDTO;
import com.cts.dto.InvoiceOutputDTO;
import com.cts.dto.LeaseInputDTO;
import com.cts.dto.LeaseOutputDTO;
import com.cts.entity.Invoice;
import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.mapper.InvoiceMapper;
import com.cts.mapper.LeaseMapper;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.LeaseRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
import com.cts.service.LeaseService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cts.entity.User;
import com.cts.exception.UserIdNotFoundException;
import com.cts.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LeaseServiceImpl implements LeaseService {

	private LeaseRepository leaseRepository;
	private UserRepository userRepository;
    private UnitRepository unitRepository;
    private TenantProfileRepository tenantProfileRepository;
    private InvoiceRepository invoiceRepository;
    
    @Override
    @Audit(action = AuditActions.CREATE_LEASE, resourceType = "Lease")
    public LeaseOutputDTO leaseGeneration(LeaseInputDTO input) {
        Unit unit = unitRepository.findById(input.getUnitId())
                .orElseThrow(() -> new UnitIdNotFoundException("Unit Id doesn't exist"));
        TenantProfile tenantProfile = tenantProfileRepository.findById(input.getTenantId())
                .orElseThrow(() -> new TenantIdNotFoundException("Tenant Id doesn't exist"));
        Lease lease = LeaseMapper.convertToLease(input, unit, tenantProfile);
        Lease savedLease = leaseRepository.save(lease);
        return LeaseMapper.convertToLeaseOutputDto(savedLease);
    }

    @Audit(action = AuditActions.CREATE_INVOICE, resourceType = "Invoice")
    private List<InvoiceOutputDTO> generateInvoice(int tenantId, int leaseId) {

        TenantProfile tenant = tenantProfileRepository.findById(tenantId)
                .orElseThrow(() -> new TenantIdNotFoundException("Tenant not found"));
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));
        LocalDate leaseStart = lease.getStartDate();
        LocalDate leaseEnd = lease.getEndDate();
        LocalDate currentMonth = leaseStart.withDayOfMonth(1);
        List<Invoice> invoices = new ArrayList<>();
        while (!currentMonth.isAfter(leaseEnd)) {
            InvoiceInputDTO input = new InvoiceInputDTO(tenantId, leaseId);
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
    @Audit(action = AuditActions.UPDATE_LEASE, resourceType = "Lease")
    public LeaseOutputDTO updateLeaseStatus(int leaseId, String status) {

        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease Id not exists"));

        // Logged-in user
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User loggedInUser = userRepository.findUserByEmail(email);

        if (loggedInUser == null) {
            throw new UserIdNotFoundException("User not found");
        }

        // Owner of the property
        int ownerUserId = lease.getUnit()
                .getProperty()
                .getUser()
                .getUserId();

        if (ownerUserId != loggedInUser.getUserId()) {
            throw new AccessDeniedException(
                    "Only the owner of this unit can update the lease status");
        }

        Lease.Status enumStatus = Lease.Status.valueOf(status);

        lease.setStatus(enumStatus);

        Lease updatedLease = leaseRepository.save(lease);

        if (enumStatus == Lease.Status.Agreed) {
            generateInvoice(
                    lease.getTenantProfile().getTenantId(),
                    leaseId);
        }

        return LeaseMapper.convertToLeaseOutputDto(updatedLease);
    }
}
