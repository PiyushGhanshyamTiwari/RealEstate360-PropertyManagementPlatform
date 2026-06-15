// MaintenanceLogMapper.java

package com.cts.mapper;
 
import com.cts.dto.*;

import com.cts.entity.MaintenanceLog;

import com.cts.entity.MaintenanceSchedule;

import org.springframework.stereotype.Component;
 
@Component

public class MaintenanceLogMapper {
 
    public MaintenanceLog convertToMaintenanceLog(

            MaintenanceLogRequestDTO dto, MaintenanceSchedule schedule) {
 
        MaintenanceLog log = new MaintenanceLog();

        log.setSchedule(schedule);

        log.setRemarks(dto.getRemarks());

        return log;

    }
 
    public MaintenanceLogResponseDTO convertToResponseDTO(MaintenanceLog log) {

    	MaintenanceLogResponseDTO response = new MaintenanceLogResponseDTO();

        response.setLogId(log.getLogId());

        response.setScheduleId(log.getSchedule().getScheduleId());

        response.setRemarks(log.getRemarks());

        response.setLogDate(log.getLogDate());

        return response;

    }

}
 