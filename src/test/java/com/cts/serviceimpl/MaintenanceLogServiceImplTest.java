package com.cts.serviceimpl;

import com.cts.dto.MaintenanceLogRequestDTO;
import com.cts.dto.MaintenanceLogResponseDTO;
import com.cts.entity.MaintenanceLog;
import com.cts.entity.MaintenanceSchedule;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.mapper.MaintenanceLogMapper;
import com.cts.repository.MaintenanceLogRepository;
import com.cts.repository.MaintenanceScheduleRepository;
import com.cts.serviceimpl.MaintenanceLogServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MaintenanceLogServiceImplTest {

    @Mock
    private MaintenanceLogRepository logRepository;

    @Mock
    private MaintenanceScheduleRepository scheduleRepository;

    @Mock
    private MaintenanceLogMapper mapper;

    @InjectMocks
    private MaintenanceLogServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Positive test: addLog succeeds
    @Test
    void testAddLog_Success() {
        MaintenanceLogRequestDTO request = new MaintenanceLogRequestDTO();
        request.setScheduleId(1);

        MaintenanceSchedule schedule = new MaintenanceSchedule();
        schedule.setScheduleId(1);

        MaintenanceLog log = new MaintenanceLog();
        MaintenanceLogResponseDTO response = new MaintenanceLogResponseDTO();

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(mapper.convertToMaintenanceLog(request, schedule)).thenReturn(log);
        when(logRepository.save(log)).thenReturn(log);
        when(mapper.convertToResponseDTO(log)).thenReturn(response);

        MaintenanceLogResponseDTO result = service.addLog(request);

        assertNotNull(result);
        verify(scheduleRepository, times(1)).findById(1);
        verify(logRepository, times(1)).save(log);
    }

    // Negative test: schedule not found
    @Test
    void testAddLog_ScheduleNotFound() {
        MaintenanceLogRequestDTO request = new MaintenanceLogRequestDTO();
        request.setScheduleId(99);

        when(scheduleRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(MaintenanceScheduleNotFoundException.class,
                () -> service.addLog(request));
    }

    // Positive test: getLogsByScheduleId returns page
    @Test
    void testGetLogsByScheduleId_Success() {
        MaintenanceLog log = new MaintenanceLog();
        MaintenanceLogResponseDTO dto = new MaintenanceLogResponseDTO();

        Page<MaintenanceLog> page = new PageImpl<>(List.of(log));

        when(logRepository.findBySchedule_ScheduleId(eq(1), any(Pageable.class)))
                .thenReturn(page);
        when(mapper.convertToResponseDTO(log)).thenReturn(dto);

        Page<MaintenanceLogResponseDTO> result = service.getLogsByScheduleId(1, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(logRepository, times(1)).findBySchedule_ScheduleId(eq(1), any(Pageable.class));
    }
}
