package com.cts.serviceimpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.dto.TechnicianInputDTO;
import com.cts.dto.TechnicianOutputDTO;
import com.cts.entity.Technician;
import com.cts.entity.User;
import com.cts.enums.TechnicianSpecialization;
import com.cts.enums.TechnicianStatus;
import com.cts.exception.TechnicianNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.TechnicianMapper;
import com.cts.repository.TechnicianRepository;
import com.cts.repository.UserRepository;
import com.cts.service.TechnicianService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final UserRepository userRepository;
    private final TechnicianMapper technicianMapper;

  
    @Override
    public TechnicianOutputDTO createTechnician(@Valid TechnicianInputDTO inputDTO) {

        User user = userRepository.findById(inputDTO.getUserId())
                .orElseThrow(() -> new UserIdNotFoundException("User not found"));

        Technician technician = new Technician();

        technician.setUser(user);

        technician.setSpecialization(
                TechnicianSpecialization.valueOf(inputDTO.getSpecialization().toUpperCase())
        );

        technician.setStatus(TechnicianStatus.ACTIVE);
        technician.setAvailable(true);

        technician.setHireDate(inputDTO.getHireDate());

        technician.setCity(inputDTO.getCity());

        technician = technicianRepository.save(technician);

        return technicianMapper.convertToTechnicianOutputDTO(technician, user);
    }

   
    @Override
    public TechnicianOutputDTO getTechnicianById(int technicianId) {

        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new TechnicianNotFoundException(
                        "Technician not found with ID: " + technicianId));

        return technicianMapper.convertToTechnicianOutputDTO(
                technician, technician.getUser());
    }

   
    @Override
    public List<TechnicianOutputDTO> getAllTechnicians(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Technician> page = technicianRepository.findAll(pageable);

        return page.getContent()
                .stream()
                .map(t -> technicianMapper.convertToTechnicianOutputDTO(
                        t, t.getUser()))
                .toList();
    }

 
    @Override
    public void deactivateTechnician(int technicianId) {

        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new TechnicianNotFoundException(
                        "Technician not found with ID: " + technicianId));

        technician.setStatus(TechnicianStatus.INACTIVE);
        technician.setAvailable(false);

        technicianRepository.save(technician);
    }

  
    @Override
    public List<TechnicianOutputDTO> getAvailableTechnicians() {

        return technicianRepository.findByAvailableTrue()
                .stream()
                .map(t -> technicianMapper.convertToTechnicianOutputDTO(
                        t, t.getUser()))
                .toList();
    }
 @Override
    public List<TechnicianOutputDTO> getTechnicianByCity(String city) {

        return technicianRepository.findByCityIgnoreCase(city)
                .stream()
                .map(t -> technicianMapper.convertToTechnicianOutputDTO(
                        t, t.getUser()))
                .toList();
    }

  
    @Override
    public List<TechnicianOutputDTO> getTechnicianBySpecialiaztion(String specialization) {

        return technicianRepository.findBySpecialization(
                TechnicianSpecialization.valueOf(specialization.toUpperCase()))
                .stream()
                .map(t -> technicianMapper.convertToTechnicianOutputDTO(
                        t, t.getUser()))
                .toList();
    }

   
    @Override
    public TechnicianOutputDTO updateTechnician(
           int technicianId,
            @Valid TechnicianInputDTO inputDTO) {

        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new TechnicianNotFoundException(
                        "Technician not found with ID: " + technicianId));

        User user = userRepository.findById(inputDTO.getUserId())
                .orElseThrow(() -> new UserIdNotFoundException("User not found"));

        technician.setUser(user);

        technician.setSpecialization(
                TechnicianSpecialization.valueOf(inputDTO.getSpecialization().toUpperCase())
        );


        technician.setCity(inputDTO.getCity());

        technician = technicianRepository.save(technician);

        return technicianMapper.convertToTechnicianOutputDTO(technician, user);
    }
}
