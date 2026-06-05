package com.cts.entity;

import java.time.LocalDate;

import org.hibernate.annotations.ValueGenerationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
	@Column(name = "user_name", nullable = false)
	private String userName;
	@Column(name="email_id", nullable= false, unique=true)
	private String emailId;
	private long phone;
	private String role;
	@Column(name = "password", nullable = false)
	private String password;
	private LocalDate registeredOn;
	
}
