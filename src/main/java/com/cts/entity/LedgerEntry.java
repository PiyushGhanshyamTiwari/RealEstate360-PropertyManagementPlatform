package com.cts.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ledger_entry")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class LedgerEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ledgerEntryId;

	// unidirectional: one ledger entry per paid invoice
	@OneToOne
	@JoinColumn(name = "invoice_id")
	private Invoice invoice;

	// unidirectional: the officer who settled the invoice
	@ManyToOne
	@JoinColumn(name = "account_officer_id")
	private AccountOfficer accountOfficer;

	private String unitType;
	private double amountPaid;
	private double profitPercent;
	private double profitAmount;
	private String description;

	@CreatedDate
	private LocalDateTime createdAt;
}