package com.cts.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.cts.entity.AccountOfficer;

import com.cts.entity.Invoice;

import com.cts.entity.LedgerEntry;

@Repository

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Integer> {

    boolean existsByInvoice(Invoice invoice);

    Optional<LedgerEntry> findByInvoice(Invoice invoice);

    List<LedgerEntry> findByAccountOfficer(AccountOfficer officer);

    @Query("""

		    select ledgerEntry 

		    from LedgerEntry ledgerEntry

		    where month(ledgerEntry.createdAt) = ?1

		      and year(ledgerEntry.createdAt) = ?2

		""")

    List<LedgerEntry> findByMonthAndYear(int month, int year);

}
