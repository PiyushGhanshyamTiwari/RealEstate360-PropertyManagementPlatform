package com.cts.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.cts.entity.Application;


import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

	@Query("SELECT a FROM Application a WHERE a.unit.unitId = :unitId")
	public List<Application> getApplicationsByUnitId(int unitId);

    @Query("SELECT a FROM Application a WHERE a.user.userId = :userId")
    public List<Application> getApplicationByTenantId(int userId);


}
