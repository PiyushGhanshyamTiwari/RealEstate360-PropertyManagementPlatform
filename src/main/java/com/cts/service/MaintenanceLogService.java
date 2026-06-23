// MaintenanceLogService.java
package com.cts.service;

import com.cts.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MaintenanceLogService {

    MaintenanceLogResponseDTO addLog(MaintenanceLogRequestDTO requestDTO);

	Page<MaintenanceLogResponseDTO> getLogsByScheduleId(int i, Pageable pageable);
}