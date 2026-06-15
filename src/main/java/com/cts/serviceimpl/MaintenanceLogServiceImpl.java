// MaintenanceLogServiceImpl.java
package com.cts.serviceimpl;
 
import com.cts.dto.MaintenanceLogRequestDTO;
import com.cts.dto.MaintenanceLogResponseDTO;
import com.cts.entity.MaintenanceLog;
import com.cts.entity.MaintenanceSchedule;
import com.cts.exception.MaintenanceLogNotFoundException;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.mapper.MaintenanceLogMapper;
import com.cts.repository.MaintenanceLogRepository;
import com.cts.repository.MaintenanceScheduleRepository;
import com.cts.service.MaintenanceLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
 
@Service
@RequiredArgsConstructor
public class MaintenanceLogServiceImpl implements MaintenanceLogService {
 
    private final MaintenanceLogRepository logRepository;
    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceLogMapper mapper;
 
    @Override
    public MaintenanceLogResponseDTO addLog(MaintenanceLogRequestDTO requestDTO) {
 
        MaintenanceSchedule schedule = scheduleRepository.findById(requestDTO.getScheduleId())
                .orElseThrow(() -> new MaintenanceScheduleNotFoundException(
                        requestDTO.getScheduleId()));
 
        MaintenanceLog log = mapper.convertToMaintenanceLog(requestDTO, schedule);
        return mapper.convertToResponseDTO(logRepository.save(log));
    }
 
	@Override
	public Page<MaintenanceLogResponseDTO> getLogsByScheduleId(int i, Pageable pageable) {
		// TODO Auto-generated method stub
		return logRepository.findBySchedule_ScheduleId(i, pageable)
                .map(log -> mapper.convertToResponseDTO(log));
	}
 
    
 
	
}