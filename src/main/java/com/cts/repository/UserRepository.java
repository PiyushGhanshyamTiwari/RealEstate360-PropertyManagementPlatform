package com.cts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cts.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select user from User user where user.emailId=?1")
	public User findUserByEmail(String emailId);

	boolean existsByEmailId(String emailId);
	


	



	
}
