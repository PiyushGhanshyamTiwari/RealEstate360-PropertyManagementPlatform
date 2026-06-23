package com.cts.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cts.entity.PropertyPhoto;
@Repository
public interface PropertyPhotoRepository extends JpaRepository<PropertyPhoto, Integer>{
	@Query("SELECT p FROM PropertyPhoto p WHERE p.unit.unitId = :unitId")
	public List<PropertyPhoto> photosByUnit(int unitId);


}
