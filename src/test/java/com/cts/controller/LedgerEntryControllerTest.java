package com.cts.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.serviceimpl.LedgerEntryServiceImpl;

@ExtendWith(MockitoExtension.class)
class LedgerEntryControllerTest extends AbstractControllerTest {

    @Mock
    private LedgerEntryServiceImpl ledgerEntryService;

    @InjectMocks
    private LedgerEntryController ledgerEntryController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/ledger";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(ledgerEntryController);
    }

    private LedgerEntryOutputDto sampleEntry() {
        return LedgerEntryOutputDto.builder()
                .ledgerEntryId(1)
                .invoiceId(10)
                .accountOfficerId(2)
                .accountOfficerName("Officer Two")
                .unitType("2BHK")
                .amountPaid(12000.0)
                .profitPercent(10.0)
                .profitAmount(1200.0)
                .description("Monthly rent payment")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET all ledger entries -> 200 with populated list")
    void shouldReturn200WhenLedgerEntriesExist() throws Exception {
        // Arrange
        when(ledgerEntryService.getAllLedgerEntries()).thenReturn(List.of(sampleEntry()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ledgerEntryId").value(1));

        verify(ledgerEntryService, times(1)).getAllLedgerEntries();
    }

    @Test
    @DisplayName("GET all ledger entries when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoLedgerEntries() throws Exception {
        // Arrange
        when(ledgerEntryService.getAllLedgerEntries()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(ledgerEntryService, times(1)).getAllLedgerEntries();
    }

    @Test
    @DisplayName("GET ledger entry by id -> 200 with entry body")
    void shouldReturn200WhenLedgerEntryFoundById() throws Exception {
        // Arrange
        when(ledgerEntryService.getLedgerEntryById(1)).thenReturn(sampleEntry());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{ledgerEntryId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ledgerEntryId").value(1))
                .andExpect(jsonPath("$.invoiceId").value(10));

        verify(ledgerEntryService, times(1)).getLedgerEntryById(1);
    }

    @Test
    @DisplayName("GET ledger entry by missing id -> service failure surfaced")
    void shouldSurfaceServerErrorWhenLedgerEntryNotFoundById() {
        // Arrange
        when(ledgerEntryService.getLedgerEntryById(99))
                .thenThrow(new RuntimeException("Ledger entry not found"));

        // Act & Assert
        assertRequestFailsWith("Ledger entry not found",
                () -> mockMvc.perform(get(BASE_URL + "/{ledgerEntryId}", 99)));

        verify(ledgerEntryService, times(1)).getLedgerEntryById(99);
    }

    @Test
    @DisplayName("GET ledger entry by non-numeric id -> 400")
    void shouldReturn400WhenLedgerEntryIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{ledgerEntryId}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ledgerEntryService);
    }

    @Test
    @DisplayName("GET ledger entry by invoice id -> 200 with entry body")
    void shouldReturn200WhenLedgerEntryFoundByInvoiceId() throws Exception {
        // Arrange
        when(ledgerEntryService.getLedgerEntryByInvoiceId(10)).thenReturn(sampleEntry());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/invoice/{invoiceId}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(10));

        verify(ledgerEntryService, times(1)).getLedgerEntryByInvoiceId(10);
    }

    @Test
    @DisplayName("GET ledger entry by missing invoice id -> service failure surfaced")
    void shouldSurfaceServerErrorWhenLedgerEntryNotFoundByInvoice() {
        // Arrange
        when(ledgerEntryService.getLedgerEntryByInvoiceId(99))
                .thenThrow(new RuntimeException("No ledger entry for this invoice"));

        // Act & Assert
        assertRequestFailsWith("No ledger entry for this invoice",
                () -> mockMvc.perform(get(BASE_URL + "/invoice/{invoiceId}", 99)));

        verify(ledgerEntryService, times(1)).getLedgerEntryByInvoiceId(99);
    }

    @Test
    @DisplayName("GET ledger entry by non-numeric invoice id -> 400")
    void shouldReturn400WhenInvoiceIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/invoice/{invoiceId}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ledgerEntryService);
    }
}
