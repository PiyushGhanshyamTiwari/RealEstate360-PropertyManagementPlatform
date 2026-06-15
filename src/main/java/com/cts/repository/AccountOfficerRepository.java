package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.entity.AccountOfficer;

@Repository
public interface AccountOfficerRepository extends JpaRepository<AccountOfficer, Integer> {
}