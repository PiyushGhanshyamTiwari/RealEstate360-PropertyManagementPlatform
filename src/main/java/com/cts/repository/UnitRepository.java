package com.cts.repository;

import java.util.List;
import com.cts.enums.UnitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.cts.entity.Unit;

@Repository

public interface UnitRepository extends JpaRepository<Unit, Integer>{

    List<Unit> findUnitByType(String type);

    List<Unit> findUnitByAreaSqFt(double areaSqFt);

    List<Unit> findUnitByFloor(int floor);

    @Query("SELECT u FROM Unit u WHERE u.rentAmount BETWEEN :minPrice AND :maxPrice")

    public List<Unit> findUnitByPriceRange(double minPrice, double maxPrice);

    @Query("SELECT u FROM Unit u WHERE u.property.propertyId = :propertyId")

    public List<Unit> findUnitByPropertyId(int propertyId);

    List<Unit> findByStatus(UnitStatus status);
}

