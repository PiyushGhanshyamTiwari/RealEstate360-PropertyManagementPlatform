package com.cts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cts.entity.Property;
import com.cts.entity.User;

public interface PropertyRepository extends JpaRepository<Property, Integer> {

	List<Property> findByPropertyCity(String propertyCity);

	List<Property> findByPropertyState(String propertyState);

	@Query("select property from Property property where property.user.userId=?1")
	List<Property> findByOwnerId(int ownerId);

	
}
