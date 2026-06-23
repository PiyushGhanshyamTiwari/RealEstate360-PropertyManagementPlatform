package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.dto.InvoiceDefaultersOutputDto;
import com.cts.dto.InvoiceOutputDTO;
import com.cts.service.InvoiceService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

   
    @Test
    void testListInvoiceWithLeaseId() throws Exception {

        when(invoiceService.listInvoiceWithLeaseId(1))
                .thenReturn(List.of(new InvoiceOutputDTO()));

        mockMvc.perform(get("/api/v1/invoice/leaseId/{leaseId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(invoiceService).listInvoiceWithLeaseId(1);
    }

    
    @Test
    void testListInvoiceWithLeaseIdEmpty() throws Exception {

        when(invoiceService.listInvoiceWithLeaseId(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/invoice/leaseId/{leaseId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    
    @Test
    void testUpdateStatus() throws Exception {

        when(invoiceService.updateStatus(1, "PAID", 10))
                .thenReturn(new InvoiceOutputDTO());

        mockMvc.perform(put("/api/v1/invoice/{invoiceId}/{status}", 1, "PAID")
                        .param("officerId", "10")) // required because controller uses @RequestParam
                .andExpect(status().isOk());

        verify(invoiceService).updateStatus(1, "PAID", 10);
    }

    
    @Test
    void testGetDefaulters() throws Exception {

        when(invoiceService.getDefaulters())
                .thenReturn(List.of(new InvoiceDefaultersOutputDto()));

        mockMvc.perform(get("/api/v1/invoice/defaulters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(invoiceService).getDefaulters();
    }

    
    @Test
    void testGetDefaultersEmpty() throws Exception {

        when(invoiceService.getDefaulters())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/invoice/defaulters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}