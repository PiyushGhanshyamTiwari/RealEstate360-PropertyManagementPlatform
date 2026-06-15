package com.cts.service;

import java.util.List;

import com.cts.dto.TechnicianInputDTO;
import com.cts.dto.TechnicianOutputDTO;

import jakarta.validation.Valid;

public interface TechnicianService  {

	TechnicianOutputDTO createTechnician(@Valid TechnicianInputDTO technicianInputDTO);

	TechnicianOutputDTO getTechnicianById(int technicianId);

	List<TechnicianOutputDTO> getAllTechnicians(int pageNo, int pageSize);

	void deactivateTechnician(int technicianId);

	List<TechnicianOutputDTO> getAvailableTechnicians();

	List<TechnicianOutputDTO> getTechnicianByCity(String city);

	List<TechnicianOutputDTO> getTechnicianBySpecialiaztion(String specialization);


	TechnicianOutputDTO updateTechnician(int technicianId, @Valid TechnicianInputDTO inputDTO);

	

	

}
