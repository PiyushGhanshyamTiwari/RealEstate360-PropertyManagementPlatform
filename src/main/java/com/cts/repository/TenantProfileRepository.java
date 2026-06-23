package com.cts.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.cts.entity.TenantProfile;

import com.cts.entity.User;

@Repository

public interface TenantProfileRepository extends JpaRepository<TenantProfile, Integer>{

    @Query("SELECT t FROM TenantProfile t WHERE t.user = :user")

    public Optional<TenantProfile> findByUser(User user);

    Optional<TenantProfile> findByUser_UserId(int userId);
}

