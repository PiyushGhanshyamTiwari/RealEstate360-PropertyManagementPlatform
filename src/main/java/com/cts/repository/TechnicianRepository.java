package com.cts.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Technician;

import com.cts.enums.TechnicianSpecialization;

public interface TechnicianRepository extends JpaRepository<Technician, Integer> {

    Optional<Technician> findByUser_UserId(int userId);
    List<Technician> findBySpecializationAndCityIgnoreCase(TechnicianSpecialization specialization,String city);

}
