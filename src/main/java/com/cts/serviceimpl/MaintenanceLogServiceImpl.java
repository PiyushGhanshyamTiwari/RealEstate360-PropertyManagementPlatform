package com.cts.serviceimpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.MaintenanceLogRequestDTO;
import com.cts.dto.MaintenanceLogResponseDTO;
import com.cts.entity.MaintenanceLog;
import com.cts.entity.MaintenanceSchedule;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.mapper.MaintenanceLogMapper;
import com.cts.repository.MaintenanceLogRepository;
import com.cts.repository.MaintenanceScheduleRepository;
import com.cts.service.MaintenanceLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaintenanceLogServiceImpl implements MaintenanceLogService {

    private final MaintenanceLogRepository logRepository;
    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceLogMapper mapper;

    @Override
    @Audit(action = AuditActions.CREATE_MAINTENANCE_LOG, resourceType = "MaintenanceLog")
    public MaintenanceLogResponseDTO addLog(MaintenanceLogRequestDTO requestDTO) {
        MaintenanceSchedule schedule = scheduleRepository.findById(requestDTO.getScheduleId())
                .orElseThrow(() -> new MaintenanceScheduleNotFoundException(requestDTO.getScheduleId()));
        MaintenanceLog log = mapper.convertToMaintenanceLog(requestDTO, schedule);
        return mapper.convertToResponseDTO(logRepository.save(log));
    }

    @Override
    public Page<MaintenanceLogResponseDTO> getLogsByScheduleId(int scheduleId, Pageable pageable) {
        return logRepository.findBySchedule_ScheduleId(scheduleId, pageable)
                .map(log -> mapper.convertToResponseDTO(log));
    }
}
