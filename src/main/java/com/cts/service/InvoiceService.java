package com.cts.service;

import java.util.List;

import com.cts.dto.InvoiceInputDTO;
import com.cts.dto.InvoiceOutputDTO;

public interface InvoiceService {

	public List<InvoiceOutputDTO> generateInvoice(InvoiceInputDTO input);

	public List<InvoiceOutputDTO> listInvoiceWithLeaseId(int leaseId);

	public InvoiceOutputDTO updateStatus(int invoiceId, String status);

}
