package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.LedgerEntryOutputDto;
import com.cts.service.LedgerEntryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class LedgerEntryControllerTest {

    @Mock
    private LedgerEntryService ledgerEntryService;

    @InjectMocks
    private LedgerEntryController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

   
    @Test
    void testGetLedgerEntryByMonthAndYear() throws Exception {

        when(ledgerEntryService.getLedgerEntryByMonthAndYear(1, 2024))
                .thenReturn(List.of(new LedgerEntryOutputDto()));

        mockMvc.perform(get("/api/v1/ledger/{month}/{year}", 1, 2024))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(ledgerEntryService).getLedgerEntryByMonthAndYear(1, 2024);
    }

    
    @Test
    void testGetLedgerEntryByMonthAndYearEmpty() throws Exception {

        when(ledgerEntryService.getLedgerEntryByMonthAndYear(1, 2024))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/ledger/{month}/{year}", 1, 2024))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testGetLedgerEntryByMonthAndYearDifferentInputs() throws Exception {

        when(ledgerEntryService.getLedgerEntryByMonthAndYear(12, 2025))
                .thenReturn(List.of(new LedgerEntryOutputDto()));

        mockMvc.perform(get("/api/v1/ledger/{month}/{year}", 12, 2025))
                .andExpect(status().isOk());

        verify(ledgerEntryService).getLedgerEntryByMonthAndYear(12, 2025);
    }
}