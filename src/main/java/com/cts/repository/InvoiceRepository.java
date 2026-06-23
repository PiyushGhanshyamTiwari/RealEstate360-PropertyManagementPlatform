package com.cts.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.cts.entity.Invoice;
import com.cts.entity.Lease;
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer>{
    @Query("SELECT i FROM Invoice i WHERE i.lease = :lease")
    public List<Invoice> findByLease(Lease lease);
    @Query("select invoice from Invoice invoice where invoice.dueDate<=now() and invoice.status='PENDING'")
    List<Invoice> findDefaulters();
}