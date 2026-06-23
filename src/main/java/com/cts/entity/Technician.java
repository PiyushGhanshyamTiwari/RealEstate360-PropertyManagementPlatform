package com.cts.entity;

import java.time.LocalDate;
import lombok.*;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.DiffExclude;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.cts.enums.TechnicianSpecialization;
import com.cts.enums.TechnicianStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name="technicians")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int technicianId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;

    @NotNull(message = "Specialization is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechnicianSpecialization specialization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechnicianStatus status;
    
    @Column(nullable = false)
    private Boolean available;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime hireDate;

    @NotBlank(message = "City cannot be empty")
    @Column(nullable = false)
    private String city;

}