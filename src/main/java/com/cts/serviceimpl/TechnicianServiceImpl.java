package com.cts.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.TechnicianInputDTO;
import com.cts.dto.TechnicianOutputDTO;
import com.cts.entity.Technician;
import com.cts.entity.User;
import com.cts.enums.TechnicianSpecialization;
import com.cts.enums.TechnicianStatus;
import com.cts.exception.TechnicianNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.MaintenanceScheduleMapper;
import com.cts.mapper.TechnicianMapper;
import com.cts.repository.MaintenanceScheduleRepository;
import com.cts.repository.MaintenanceScheduleRepository;
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
    private final MaintenanceScheduleRepository scheduleRepository;
    private final MaintenanceScheduleMapper scheduleMapper;

    @Override
    @Audit(action = AuditActions.CREATE_TECHNICIAN, resourceType = "Technician")
    public TechnicianOutputDTO createTechnician(@Valid TechnicianInputDTO inputDTO) {

        User user = userRepository.findById(inputDTO.getUserId())

                .orElseThrow(() -> new UserIdNotFoundException("User not found"));

        Technician technician = new Technician();

        technician.setUser(user);

        technician.setSpecialization(

                TechnicianSpecialization.valueOf(inputDTO.getSpecialization().toUpperCase()));
        technician.setHireDate(LocalDateTime.now());

        technician.setCity(inputDTO.getCity());

        technician = technicianRepository.save(technician);

        return technicianMapper.convertToTechnicianOutputDTO(technician, user);

    }

//    @Override
//    @Audit(action = AuditActions.UPDATE_TECHNICIAN, resourceType = "Technician")
//    public TechnicianOutputDTO updateTechnician(int technicianId, @Valid TechnicianInputDTO inputDTO) {
//        Technician technician = technicianRepository.findById(technicianId)
//                .orElseThrow(() -> new TechnicianNotFoundException(
//                        "Technician not found with ID: " + technicianId));
//        User user = userRepository.findById(inputDTO.getUserId())
//                .orElseThrow(() -> new UserIdNotFoundException("User not found"));
//        technician.setUser(user);
//        technician.setSpecialization(
//                TechnicianSpecialization.valueOf(inputDTO.getSpecialization().toUpperCase()));
//        technician.setCity(inputDTO.getCity());
//        technician = technicianRepository.save(technician);
//        return technicianMapper.convertToTechnicianOutputDTO(technician, user);
//    }

//    @Override
//    @Audit(action = AuditActions.DELETE_TECHNICIAN, resourceType = "Technician")
//    public void deactivateTechnician(int technicianId) {
//        Technician technician = technicianRepository.findById(technicianId)
//                .orElseThrow(() -> new TechnicianNotFoundException(
//                        "Technician not found with ID: " + technicianId));
//        technician.setStatus(TechnicianStatus.INACTIVE);
//        technician.setAvailable(false);
//        technicianRepository.save(technician);
//    }

    @Override
    public TechnicianOutputDTO getTechnicianById(int userid) {

        Technician technician = technicianRepository.findByUser_UserId(userid)

                .orElseThrow(() -> new TechnicianNotFoundException(

                        "Technician not found with ID: " + userid));

        return technicianMapper.convertToTechnicianOutputDTO(

                technician, technician.getUser());

    }

    @Override
    public List<TechnicianOutputDTO> getAllTechnicians() {

        return technicianRepository.findAll()

                .stream()

                .map(t -> technicianMapper.convertToTechnicianOutputDTO(t, t.getUser()))

                .collect(Collectors.toList());

    }
    

    @Override
    public List<TechnicianOutputDTO> getTechnicianBySpecializationAndCity(String specialization, String city) {

        return technicianRepository.findBySpecializationAndCityIgnoreCase(

                        TechnicianSpecialization.valueOf(specialization.toUpperCase()),

                        city)

                .stream()

                .map(t -> technicianMapper.convertToTechnicianOutputDTO(t, t.getUser()))

                .collect(Collectors.toList());

    }
    
    @Override
    public List<MaintenanceScheduleResponseDTO> getWorkHistory(int userid) {

        technicianRepository.findByUser_UserId(userid)

                .orElseThrow(() -> new TechnicianNotFoundException(

                        "Technician not found with ID: " + userid));



        return scheduleRepository.findByTechnicianUserUserId(userid)

                .stream()

                .map(schedule -> scheduleMapper.convertToResponseDTO(schedule))

                .collect(Collectors.toList());

    }
}
