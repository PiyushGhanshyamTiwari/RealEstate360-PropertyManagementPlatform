package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cts.dto.LeaseOutputDTO;
import com.cts.service.LeaseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class LeaseControllerTest {

    @Mock
    private LeaseService leaseService;

    @InjectMocks
    private LeaseController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    
    @Test
    void testUpdateLeaseStatus() throws Exception {

        when(leaseService.updateLeaseStatus(1, "Agreed"))
                .thenReturn(new LeaseOutputDTO());

        mockMvc.perform(put("/api/v1/lease/{leaseId}/{status}", 1, "Agreed"))
                .andExpect(status().isOk());

        verify(leaseService).updateLeaseStatus(1, "Agreed");
    }

    
    @Test
    void testUpdateLeaseStatusDifferentStatus() throws Exception {

        when(leaseService.updateLeaseStatus(2, "Pending"))
                .thenReturn(new LeaseOutputDTO());

        mockMvc.perform(put("/api/v1/lease/{leaseId}/{status}", 2, "Pending"))
                .andExpect(status().isOk());

        verify(leaseService).updateLeaseStatus(2, "Pending");
    }
}