package com.cts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.dto.AccountOfficerInputDto;
import com.cts.dto.AccountOfficerOutputDto;
import com.cts.dto.LedgerEntryOutputDto;
import com.cts.serviceimpl.AccountOfficerServiceImpl;

@ExtendWith(MockitoExtension.class)
class AccountOfficerControllerTest extends AbstractControllerTest {

    @Mock
    private AccountOfficerServiceImpl accountOfficerService;

    @InjectMocks
    private AccountOfficerController accountOfficerController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/account-officer";

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(accountOfficerController);
    }

    private AccountOfficerOutputDto sampleOfficer() {
        return AccountOfficerOutputDto.builder()
                .officerId(1)
                .fullName("Asha Rao")
                .emailId("asha@cts.com")
                .phone(9876543210L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private AccountOfficerInputDto sampleInput() {
        return AccountOfficerInputDto.builder()
                .fullName("Asha Rao")
                .emailId("asha@cts.com")
                .phone(9876543210L)
                .build();
    }

    @Test
    @DisplayName("POST officer -> 201 with created officer body")
    void shouldReturn201WhenOfficerCreated() throws Exception {
        // Arrange
        when(accountOfficerService.addOfficer(any(AccountOfficerInputDto.class)))
                .thenReturn(sampleOfficer());

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(sampleInput())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.officerId").value(1))
                .andExpect(jsonPath("$.fullName").value("Asha Rao"))
                .andExpect(jsonPath("$.emailId").value("asha@cts.com"));

        verify(accountOfficerService, times(1)).addOfficer(any(AccountOfficerInputDto.class));
    }

    @Test
    @DisplayName("POST officer with malformed JSON -> 400 and service not invoked")
    void shouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        // Arrange / Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not valid json "))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountOfficerService);
    }

    @Test
    @DisplayName("GET all officers -> 200 with populated list")
    void shouldReturn200WhenOfficersExist() throws Exception {
        // Arrange
        when(accountOfficerService.getAllOfficers()).thenReturn(List.of(sampleOfficer()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].officerId").value(1));

        verify(accountOfficerService, times(1)).getAllOfficers();
    }

    @Test
    @DisplayName("GET all officers when none exist -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoOfficers() throws Exception {
        // Arrange
        when(accountOfficerService.getAllOfficers()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(accountOfficerService, times(1)).getAllOfficers();
    }

    @Test
    @DisplayName("GET officer by id -> 200 with officer body")
    void shouldReturn200WhenOfficerFoundById() throws Exception {
        // Arrange
        when(accountOfficerService.getOfficerById(1)).thenReturn(sampleOfficer());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{officerId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.officerId").value(1))
                .andExpect(jsonPath("$.emailId").value("asha@cts.com"));

        verify(accountOfficerService, times(1)).getOfficerById(1);
    }

    @Test
    @DisplayName("GET officer by id when missing -> service failure surfaced")
    void shouldSurfaceServerErrorWhenOfficerNotFound() {
        // Arrange
        when(accountOfficerService.getOfficerById(99))
                .thenThrow(new RuntimeException("Account officer not found"));

        // Act & Assert
        assertRequestFailsWith("Account officer not found",
                () -> mockMvc.perform(get(BASE_URL + "/{officerId}", 99)));

        verify(accountOfficerService, times(1)).getOfficerById(99);
    }

    @Test
    @DisplayName("GET officer by id with non-numeric id -> 400 and service not invoked")
    void shouldReturn400WhenOfficerIdIsNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{officerId}", "not-a-number"))
                .andExpect(status().isBadRequest());

        verify(accountOfficerService, never()).getOfficerById(org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    @DisplayName("GET officer ledger entries -> 200 with populated list")
    void shouldReturn200WhenLedgerEntriesExist() throws Exception {
        // Arrange
        LedgerEntryOutputDto entry = LedgerEntryOutputDto.builder()
                .ledgerEntryId(5)
                .invoiceId(10)
                .accountOfficerId(1)
                .amountPaid(1500.0)
                .build();
        when(accountOfficerService.getOfficerLedgerEntries(1)).thenReturn(List.of(entry));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{officerId}/ledger-entries", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].ledgerEntryId").value(5));

        verify(accountOfficerService, times(1)).getOfficerLedgerEntries(1);
    }

    @Test
    @DisplayName("GET officer ledger entries when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoLedgerEntries() throws Exception {
        // Arrange
        when(accountOfficerService.getOfficerLedgerEntries(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{officerId}/ledger-entries", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(accountOfficerService, times(1)).getOfficerLedgerEntries(1);
    }
}
