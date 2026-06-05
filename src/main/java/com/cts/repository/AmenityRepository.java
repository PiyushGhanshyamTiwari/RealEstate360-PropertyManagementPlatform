package com.cts.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cts.entity.Amenity;
import com.cts.entity.Unit;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer>{

	@Query("SELECT amenity FROM Amenity amenity WHERE amenity.unit.unitId = ?1")
    List<Amenity> findByUnitId(int unitId);
	@Query("SELECT amenity FROM Amenity amenity WHERE amenity.name = ?1")
    List<Amenity> findByName(String name);

}
