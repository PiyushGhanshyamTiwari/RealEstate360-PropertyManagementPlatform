package com.cts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.AccountOfficer;

@Repository
public interface AccountOfficerRepository extends JpaRepository<AccountOfficer, Integer> {
	 Optional<AccountOfficer> findByUserUserId(int userId);
}