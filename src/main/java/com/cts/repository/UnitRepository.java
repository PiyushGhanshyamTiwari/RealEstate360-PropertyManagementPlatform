package com.cts.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cts.dto.UnitOutputDTO;
import com.cts.entity.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Integer> {

	List<Unit> findUnitByType(String type);

	List<Unit> findUnitByAreaSqFt(double areaSqFt);


	List<Unit> findUnitByRentAmountBetween(double min, double max);

	List<Unit> findUnitByFloor(int floor);
	
//	@Query("SELECT u from Unit u where u.property.propertyId=:propertyId")
//	List<Unit> findUnitByPropertyId(int propertyId);
//	List<Unit> findUnitByPropertyPropertyId(int propertyId); //alternative
	

    @Query("SELECT unit FROM Unit unit WHERE unit.property.propertyId = :propertyId")
    public List<Unit> findUnitByPropertyId(int propertyId);

	

}
