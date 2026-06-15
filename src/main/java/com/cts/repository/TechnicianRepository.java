package com.cts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.entity.Technician;
import com.cts.entity.User;
import com.cts.enums.TechnicianSpecialization;

public interface TechnicianRepository extends JpaRepository<Technician, Integer> {
	List<Technician> findByAvailableTrue();
	List<Technician> findByCityIgnoreCase(String city);
	List<Technician> findBySpecialization(TechnicianSpecialization specialization);
	
}
