package com.cts.service;

import java.util.List;

import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TechnicianInputDTO;
import com.cts.dto.TechnicianOutputDTO;

import jakarta.validation.Valid;

public interface TechnicianService  {

	TechnicianOutputDTO createTechnician(@Valid TechnicianInputDTO technicianInputDTO);

    TechnicianOutputDTO getTechnicianById(int userid);

    List<TechnicianOutputDTO> getAllTechnicians();

    List<TechnicianOutputDTO> getTechnicianBySpecializationAndCity(String specialization, String city);

    List<MaintenanceScheduleResponseDTO> getWorkHistory(int technicianId);

	//TechnicianOutputDTO updateTechnician(int technicianId, @Valid TechnicianInputDTO inputDTO);

	

	

}
