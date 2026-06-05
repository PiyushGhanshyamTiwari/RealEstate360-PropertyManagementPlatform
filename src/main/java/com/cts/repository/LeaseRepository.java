package com.cts.repository;


import com.cts.entity.Lease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface LeaseRepository extends JpaRepository<Lease, Integer>{

}
