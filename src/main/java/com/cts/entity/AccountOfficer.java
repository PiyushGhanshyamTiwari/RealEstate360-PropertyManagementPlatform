package com.cts.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_officer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AccountOfficer {

	@Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int officerId;

    @OneToOne

    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")

    private User user;

    private String fullName;

    private String address;

    @CreatedDate

    private LocalDateTime createdAt;
}