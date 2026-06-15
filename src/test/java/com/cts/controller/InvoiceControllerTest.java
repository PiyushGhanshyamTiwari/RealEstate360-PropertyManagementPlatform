package com.cts.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.dto.InvoiceInputDTO;

import com.cts.dto.InvoiceOutputDTO;

import com.cts.exception.TenantIdNotFoundException;
import com.cts.serviceimpl.InvoiceServiceImpl;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest extends AbstractControllerTest {

    @Mock
    private InvoiceServiceImpl invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/invoice";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(invoiceController);
    }

    private InvoiceOutputDTO sampleInvoice() {
        return InvoiceOutputDTO.builder()
                .invoiceId(1)
                .tenantId(5)
                .leaseId(7)
                .periodStart(LocalDate.of(2026, 1, 1))
                .periodEnd(LocalDate.of(2026, 1, 31))
                .amountDue(12000.0)
                .dueDate(LocalDate.of(2026, 2, 5))
                .status("PENDING")
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private InvoiceInputDTO validInput() {
        return InvoiceInputDTO.builder().tenantId(5).leaseId(7).build();
    }

    @Test
    @DisplayName("POST generate invoice -> 201 with generated invoice list")
    void shouldReturn201WhenInvoiceGenerated() throws Exception {
        // Arrange
        when(invoiceService.generateInvoice(any(InvoiceInputDTO.class)))
                .thenReturn(List.of(sampleInvoice()));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].invoiceId").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(invoiceService, times(1)).generateInvoice(any(InvoiceInputDTO.class));
    }

    @Test
    @DisplayName("POST generate invoice with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenBodyIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ bad"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    @DisplayName("POST generate invoice for missing tenant -> 404 Not Found")
    void shouldReturn404WhenTenantNotFound() throws Exception {
        // Arrange
        when(invoiceService.generateInvoice(any(InvoiceInputDTO.class)))
                .thenThrow(new TenantIdNotFoundException("Tenant not found"));

        // Act & Assert
        mockMvc.perform(post(BASE_URL + "/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(validInput())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Tenant not found")));

        verify(invoiceService, times(1)).generateInvoice(any(InvoiceInputDTO.class));
    }

    @Test
    @DisplayName("GET invoices by lease -> 200 with list")
    void shouldReturn200WhenInvoicesExistForLease() throws Exception {
        // Arrange
        when(invoiceService.listInvoiceWithLeaseId(7)).thenReturn(List.of(sampleInvoice()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/leaseId/{leaseId}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leaseId").value(7));

        verify(invoiceService, times(1)).listInvoiceWithLeaseId(7);
    }

    @Test
    @DisplayName("GET invoices by lease when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoInvoices() throws Exception {
        // Arrange
        when(invoiceService.listInvoiceWithLeaseId(7)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/leaseId/{leaseId}", 7))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(invoiceService, times(1)).listInvoiceWithLeaseId(7);
    }

    @Test
    @DisplayName("GET invoices by lease with non-numeric id -> 400")
    void shouldReturn400WhenLeaseIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/leaseId/{leaseId}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(invoiceService);
    }

    @Test
    @DisplayName("PUT invoice status -> 200 with updated invoice")
    void shouldReturn200WhenInvoiceStatusUpdated() throws Exception {
        // Arrange
        InvoiceOutputDTO paid = sampleInvoice();
        paid.setStatus("PAID");
        when(invoiceService.updateStatus(eq(1), eq("PAID"),eq(1))).thenReturn(paid);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{invoiceId}/{status}", 1, "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));

        verify(invoiceService, times(1)).updateStatus(eq(1), eq("PAID"),eq(1));
    }

    @Test
    @DisplayName("PUT invoice status for missing invoice -> service failure surfaced")
    void shouldSurfaceServerErrorWhenInvoiceNotFoundOnUpdate() {
        // Arrange
        when(invoiceService.updateStatus(eq(99), eq("PAID"),eq(1)))
                .thenThrow(new RuntimeException("Invoice Id not found"));

        // Act & Assert
        assertRequestFailsWith("Invoice Id not found",
                () -> mockMvc.perform(put(BASE_URL + "/{invoiceId}/{status}", 99, "PAID")));

        verify(invoiceService, times(1)).updateStatus(eq(99), eq("PAID"),eq(1));
    }
}
