package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.MaintenanceScheduleRequestDTO;
import com.cts.dto.MaintenanceScheduleResponseDTO;
import com.cts.dto.ManagerAssignRequestDTO;
import com.cts.dto.TechnicianStatusUpdateDTO;
import com.cts.dto.TenantIssueRequestDTO;
import com.cts.entity.MaintenanceSchedule;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.entity.Technician;
import com.cts.enums.IssueCategory;
import com.cts.enums.MaintenanceStatus;
import com.cts.enums.Severity;
import com.cts.enums.TechnicianSpecialization;
import com.cts.exception.InvalidStatusTransitionException;
import com.cts.exception.MaintenanceScheduleNotFoundException;
import com.cts.exception.NoTechnicianAssignedException;
import com.cts.exception.SpecializationMismatchException;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnauthorizedTechnicianException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.exception.TechnicianNotFoundException;
import com.cts.mapper.MaintenanceScheduleMapper;
import com.cts.repository.MaintenanceScheduleRepository;
import com.cts.repository.TechnicianRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
import com.cts.service.MaintenanceScheduleService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaintenanceScheduleServiceImpl implements MaintenanceScheduleService {

	private final MaintenanceScheduleRepository scheduleRepository;
	private final TenantProfileRepository tenantRepository;
	private final UnitRepository unitRepository;
	private final TechnicianRepository technicianRepository;
	private final MaintenanceScheduleMapper mapper;

	@Override
	@Audit(action = AuditActions.CREATE_MAINTENANCE_SCHEDULE, resourceType = "MaintenanceSchedule")
	@Transactional
	public MaintenanceScheduleResponseDTO createByTenant(TenantIssueRequestDTO requestDTO) {

		TenantProfile tenant = tenantRepository.findByUser_UserId(requestDTO.getUserId())

				.orElseThrow(() -> new TenantIdNotFoundException(

						"Tenant not found with ID: " + requestDTO.getUserId()));

		Unit unit = unitRepository.findById(requestDTO.getUnitId())

				.orElseThrow(() -> new UnitIdNotFoundException(

						"Unit not found with ID: " + requestDTO.getUnitId()));
		MaintenanceSchedule schedule = mapper.convertToMaintenanceSchedule(requestDTO, tenant, unit);

		return mapper.convertToResponseDTO(scheduleRepository.save(schedule));
	}

	@Override
	@Audit(action = AuditActions.UPDATE_MAINTENANCE_SCHEDULE, resourceType = "MaintenanceSchedule")
	@Transactional
	public MaintenanceScheduleResponseDTO assignByManager(int scheduleId,

			ManagerAssignRequestDTO requestDTO) {

		MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)

				.orElseThrow(() -> new MaintenanceScheduleNotFoundException(scheduleId));

// Must be OPEN to assign

		if (!schedule.getStatus().equals(MaintenanceStatus.OPEN)) {

			throw new InvalidStatusTransitionException(

					schedule.getStatus().name(), MaintenanceStatus.ASSIGNED.name());

		}

		Technician technician = technicianRepository.findByUser_UserId(requestDTO.getUserId())

				.orElseThrow(() -> new TechnicianNotFoundException(

						"Technician not found with ID: " + requestDTO.getUserId()));

// Check specialization matches category

		if (!matchesSpecialization(schedule.getCategory(), technician.getSpecialization())) {

			throw new SpecializationMismatchException(

					"Specialization mismatch - Issue category is " + schedule.getCategory() +

							" but technician is " + technician.getSpecialization());

		}

// Assign (technician can have multiple tasks)

		schedule.setTechnician(technician);

		schedule.setSeverity(Severity.valueOf(requestDTO.getSeverity().toUpperCase()));

		schedule.setStatus(MaintenanceStatus.ASSIGNED);

		return mapper.convertToResponseDTO(scheduleRepository.save(schedule));

	}

	@Override
	@Transactional
	@Audit(action = AuditActions.UPDATE_MAINTENANCE_SCHEDULE, resourceType = "MaintenanceSchedule")
	public MaintenanceScheduleResponseDTO updateByTechnician(int scheduleId,

			int userId, TechnicianStatusUpdateDTO requestDTO) {

		MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)

				.orElseThrow(() -> new MaintenanceScheduleNotFoundException(scheduleId));

// Check technician assigned

		if (schedule.getTechnician() == null) {

			throw new NoTechnicianAssignedException(

					"Cannot update status - no technician assigned to this schedule");

		}

		Technician assignedTechnician = schedule.getTechnician();

// Identity verification

		if (assignedTechnician.getUser().getUserId() != userId) {

			throw new UnauthorizedTechnicianException(

					"Access denied - you can only update schedules assigned to you");

		}

		MaintenanceStatus newStatus = MaintenanceStatus.valueOf(

				requestDTO.getStatus().toUpperCase());

// Validate status transition

		validateStatusTransition(schedule.getStatus(), newStatus);

		schedule.setStatus(newStatus);

		return mapper.convertToResponseDTO(scheduleRepository.save(schedule));

	}

	@Override
	public MaintenanceScheduleResponseDTO getScheduleById(int scheduleId) {

		MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)

				.orElseThrow(() -> new MaintenanceScheduleNotFoundException(scheduleId));

		return mapper.convertToResponseDTO(schedule);

	}

	@Override
	public List<MaintenanceScheduleResponseDTO> getAllSchedules(String status, String severity) {

		if (status != null && !status.isBlank()) {

			return scheduleRepository

					.findByStatus(MaintenanceStatus.valueOf(status.toUpperCase()))

					.stream()

					.map(mapper::convertToResponseDTO)

					.collect(Collectors.toList());

		}

		if (severity != null && !severity.isBlank()) {

			return scheduleRepository

					.findBySeverity(Severity.valueOf(severity.toUpperCase()))

					.stream()

					.map(mapper::convertToResponseDTO)

					.collect(Collectors.toList());

		}

		return scheduleRepository.findAll()

				.stream()

				.map(mapper::convertToResponseDTO)

				.collect(Collectors.toList());

	}

	private boolean matchesSpecialization(IssueCategory category, TechnicianSpecialization spec) {

		if (category == null)
			return true;

		switch (category) {

		case ELECTRICAL:
			return spec == TechnicianSpecialization.ELECTRICIAN;

		case PLUMBING:
			return spec == TechnicianSpecialization.PLUMBER;

		case CLEANING:
			return spec == TechnicianSpecialization.HOUSE_CLEANER;

		case PAINTING:
			return spec == TechnicianSpecialization.PAINTER;

		case CARPENTRY:
			return spec == TechnicianSpecialization.CARPENTER;

		default:
			return false;

		}

	}

	private void validateStatusTransition(MaintenanceStatus current, MaintenanceStatus next) {

		if (current.equals(MaintenanceStatus.ASSIGNED) && !next.equals(MaintenanceStatus.IN_PROGRESS)) {

			throw new InvalidStatusTransitionException(current.name(), next.name());

		}

		if (current.equals(MaintenanceStatus.IN_PROGRESS) && !next.equals(MaintenanceStatus.RESOLVED)) {

			throw new InvalidStatusTransitionException(current.name(), next.name());

		}

		if (current.equals(MaintenanceStatus.RESOLVED) && !next.equals(MaintenanceStatus.CLOSED)) {

			throw new InvalidStatusTransitionException(current.name(), next.name());

		}

		if (current.equals(MaintenanceStatus.CLOSED)) {

			throw new InvalidStatusTransitionException(current.name(), next.name());

		}

	}

}
