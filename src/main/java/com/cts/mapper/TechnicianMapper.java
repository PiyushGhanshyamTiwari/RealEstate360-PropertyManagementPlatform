package com.cts.mapper;

import com.cts.dto.TechnicianInputDTO;
import com.cts.dto.TechnicianOutputDTO;
import com.cts.entity.Technician;
import com.cts.entity.User;
import com.cts.enums.TechnicianSpecialization;

import org.springframework.stereotype.Component;

@Component
public class TechnicianMapper {

    public Technician convertToTechnician(TechnicianInputDTO dto, User user) {

        Technician technician = new Technician();

        technician.setUser(user);

     
        technician.setSpecialization(
                TechnicianSpecialization.valueOf(
                        dto.getSpecialization().toUpperCase()
                )
        );

       technician.setCity(dto.getCity());
       technician.setHireDate(dto.getHireDate());

        return technician;
    }

    public TechnicianOutputDTO convertToTechnicianOutputDTO(Technician technician,User user) {

        TechnicianOutputDTO response = new TechnicianOutputDTO();

        response.setTechnicianId(technician.getTechnicianId());
        response.setUserId(technician.getUser().getUserId());

        response.setSpecialization(
               technician.getSpecialization().name()
        );

        response.setStatus(
                technician.getStatus().name()
        );
        response.setAvailable(technician.getAvailable());

        response.setHireDate(technician.getHireDate());
        response.setCity(technician.getCity());

        return response;
    }
}