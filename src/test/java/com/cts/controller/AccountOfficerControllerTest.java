package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.AccountOfficerInputDto;
import com.cts.dto.AccountOfficerOutputDto;
import com.cts.dto.LedgerEntryOutputDto;
import com.cts.service.AccountOfficerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AccountOfficerControllerTest {

    @Mock
    private AccountOfficerService service;

    @InjectMocks
    private AccountOfficerController controller;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

   
    @Test
    void testAddOfficer() throws Exception {
        AccountOfficerInputDto input = new AccountOfficerInputDto();
        input.setUserId(1);

        AccountOfficerOutputDto output = new AccountOfficerOutputDto();

        when(service.addOfficer(any())).thenReturn(output);

        mockMvc.perform(post("/api/v1/account-officer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated());

        verify(service).addOfficer(any());
    }

    
    @Test
    void testGetAllOfficers() throws Exception {
        when(service.getAllOfficers())
                .thenReturn(List.of(new AccountOfficerOutputDto()));

        mockMvc.perform(get("/api/v1/account-officer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(service).getAllOfficers();
    }

    
    @Test
    void testGetOfficerById() throws Exception {
        when(service.getOfficerById(1))
                .thenReturn(new AccountOfficerOutputDto());

        mockMvc.perform(get("/api/v1/account-officer/1"))
                .andExpect(status().isOk());

        verify(service).getOfficerById(1);
    }

    
    @Test
    void testGetOfficerLedgerEntries() throws Exception {
        when(service.getOfficerLedgerEntries(1))
                .thenReturn(List.of(new LedgerEntryOutputDto()));

        mockMvc.perform(get("/api/v1/account-officer/1/ledger-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(service).getOfficerLedgerEntries(1);
    }

   
    @Test
    void testGetOfficerLedgerEntriesEmpty() throws Exception {
        when(service.getOfficerLedgerEntries(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/account-officer/1/ledger-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}