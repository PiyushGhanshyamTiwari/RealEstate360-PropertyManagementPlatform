package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.cts.dto.MaintenanceLogRequestDTO;
import com.cts.dto.MaintenanceLogResponseDTO;
import com.cts.entity.MaintenanceLog;
import com.cts.entity.MaintenanceSchedule;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.mapper.MaintenanceLogMapper;
import com.cts.repository.MaintenanceLogRepository;
import com.cts.repository.MaintenanceScheduleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceLogServiceImplTest {

    @Mock
    private MaintenanceLogRepository logRepository;

    @Mock
    private MaintenanceScheduleRepository scheduleRepository;

    @Mock
    private MaintenanceLogMapper mapper;

    @InjectMocks
    private MaintenanceLogServiceImpl service;

    private MaintenanceSchedule schedule;
    private MaintenanceLog log;
    private MaintenanceLogResponseDTO responseDTO;

    @BeforeEach
    void setup() {
        schedule = new MaintenanceSchedule();
        schedule.setScheduleId(1);

        log = new MaintenanceLog();
        log.setSchedule(schedule);

        responseDTO = new MaintenanceLogResponseDTO();
    }

    
    @Test
    void testAddLogSuccess() {
        MaintenanceLogRequestDTO request = new MaintenanceLogRequestDTO();
        request.setScheduleId(1);

        when(scheduleRepository.findById(1)).thenReturn(Optional.of(schedule));
        when(mapper.convertToMaintenanceLog(request, schedule)).thenReturn(log);
        when(logRepository.save(log)).thenReturn(log);
        when(mapper.convertToResponseDTO(log)).thenReturn(responseDTO);

        MaintenanceLogResponseDTO result = service.addLog(request);

        assertNotNull(result);
        verify(scheduleRepository).findById(1);
        verify(logRepository).save(log);
    }

    
    @Test
    void testAddLogScheduleNotFound() {
        MaintenanceLogRequestDTO request = new MaintenanceLogRequestDTO();
        request.setScheduleId(1);

        when(scheduleRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(MaintenanceScheduleNotFoundException.class,
                () -> service.addLog(request));

        verify(logRepository, never()).save(any());
    }

   
    @Test
    void testGetLogsByScheduleIdWithData() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<MaintenanceLog> logPage =
                new PageImpl<>(List.of(log), pageable, 1);

        when(logRepository.findBySchedule_ScheduleId(1, pageable))
                .thenReturn(logPage);

        when(mapper.convertToResponseDTO(log))
                .thenReturn(responseDTO);

        Page<MaintenanceLogResponseDTO> result =
                service.getLogsByScheduleId(1, pageable);

        assertEquals(1, result.getTotalElements());
        verify(logRepository).findBySchedule_ScheduleId(1, pageable);
    }

    
    @Test
    void testGetLogsByScheduleIdEmpty() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<MaintenanceLog> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);

        when(logRepository.findBySchedule_ScheduleId(1, pageable))
                .thenReturn(emptyPage);

        Page<MaintenanceLogResponseDTO> result =
                service.getLogsByScheduleId(1, pageable);

        assertTrue(result.isEmpty());
    }
}
