package com.cts.serviceimpl;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cts.annotation.Audit;
import com.cts.constants.AuditActions;
import com.cts.dto.ApplicationInputDTO;
import com.cts.dto.ApplicationOutputDTO;
import com.cts.entity.Application;
import com.cts.entity.Lease;
import com.cts.entity.TenantProfile;
import com.cts.entity.Unit;
import com.cts.entity.User;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.ApplicationMapper;
import com.cts.mapper.LeaseMapper;
import com.cts.repository.ApplicationRepository;
import com.cts.repository.LeaseRepository;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UnitRepository;
import com.cts.repository.UserRepository;
import com.cts.service.ApplicationService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;


import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

	private ApplicationRepository applicationRepository;
    private UnitRepository unitRepository;
    private UserRepository userRepository;
    private TenantProfileRepository tenantProfileRepository;
    private LeaseRepository leaseRepository;

    @Override
    @Audit(action = AuditActions.CREATE_APPLICATION, resourceType = "Application")
    public ApplicationOutputDTO submitApplication(ApplicationInputDTO input) {

        Unit unit = unitRepository.findById(input.getUnitId())
                .orElseThrow(() -> new UnitIdNotFoundException("UnitId not found"));
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new UserIdNotFoundException("UserId not found"));
        Application application = ApplicationMapper.convertToApllication(input, unit, user);
        Application savedApplication = applicationRepository.save(application);
        return ApplicationMapper.convertToApplicationOutputDto(savedApplication);
    }

    @Override
    public List<ApplicationOutputDTO> getApplicationsByUnitId(int unitId) {
        return applicationRepository.getApplicationsByUnitId(unitId)
                .stream()
                .map(ApplicationMapper::convertToApplicationOutputDto)
                .collect(Collectors.toList());
    }

    @Override
    @Audit(action = AuditActions.UPDATE_APPLICATION, resourceType = "Application")
    public ApplicationOutputDTO updateStatusOfApplication(int applicationId, String status) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Logged-in user
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User loggedInUser = userRepository.findUserByEmail(email);

        if (loggedInUser == null) {
            throw new UserIdNotFoundException("User not found");
        }

        // Owner of the property associated with the unit
        int ownerUserId = app.getUnit()
                .getProperty()
                .getUser()
                .getUserId();

        if (ownerUserId != loggedInUser.getUserId()) {
            throw new AccessDeniedException(
                    "Only the owner of this unit can update the application status");
        }

        Application.Status enumStatus = Application.Status.valueOf(status);

        app.setStatus(enumStatus);

        Application updatedApp = applicationRepository.save(app);

        if (enumStatus == Application.Status.Approved) {

            boolean exists = leaseRepository
                    .existsByUnit_UnitIdAndTenantProfile_TenantId(
                            app.getUnit().getUnitId(),
                            app.getUser().getUserId());

            if (!exists) {

                TenantProfile tenantProfile = tenantProfileRepository
                        .findByUser_UserId(app.getUser().getUserId())
                        .orElseThrow(() ->
                                new TenantIdNotFoundException("Tenant not found"));

                Lease lease = LeaseMapper.convertFromApplication(app, tenantProfile);

                leaseRepository.save(lease);
            }
        }

        return ApplicationMapper.convertToApplicationOutputDto(updatedApp);
    }

    @Override
    public List<ApplicationOutputDTO> getApplicationByTenantId(int userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("No Tenant Id found"));

        return applicationRepository.getApplicationByTenantId(userId)
                .stream()
                .map(ApplicationMapper::convertToApplicationOutputDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationOutputDTO getApplicationByApplicationId(int applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        return ApplicationMapper.convertToApplicationOutputDto(app);
    }

}
