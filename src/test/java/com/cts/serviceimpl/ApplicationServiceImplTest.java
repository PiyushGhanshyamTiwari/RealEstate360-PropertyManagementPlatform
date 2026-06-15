package com.cts.serviceimpl;

import com.cts.dto.ApplicationInputDTO;
import com.cts.dto.ApplicationOutputDTO;
import com.cts.entity.Application;
import com.cts.entity.Unit;
import com.cts.entity.User;
import com.cts.exception.UnitIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.ApplicationMapper;
import com.cts.repository.ApplicationRepository;
import com.cts.repository.UnitRepository;
import com.cts.repository.UserRepository;
import com.cts.serviceimpl.ApplicationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitApplication_Success() {
        ApplicationInputDTO inputDto = new ApplicationInputDTO();
        inputDto.setUnitId(1);
        inputDto.setUserId(10);

        Unit unit = new Unit();
        unit.setUnitId(1);

        User user = new User();
        user.setUserId(10);

        Application application = new Application();
        application.setApplicationId(100);

        ApplicationOutputDTO outputDto = new ApplicationOutputDTO();
        outputDto.setApplicationId(100);

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(userRepository.findById(10)).thenReturn(Optional.of(user));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        // ApplicationMapper exposes static methods, so it is stubbed via a
        // try-with-resources MockedStatic that is closed at the end of the test.
        try (MockedStatic<ApplicationMapper> mockedMapper = mockStatic(ApplicationMapper.class)) {
            mockedMapper.when(() -> ApplicationMapper.convertToApllication(inputDto, unit, user))
                    .thenReturn(application);
            mockedMapper.when(() -> ApplicationMapper.convertToApplicationOutputDto(application))
                    .thenReturn(outputDto);

            ApplicationOutputDTO result = applicationService.submitApplication(inputDto);

            assertNotNull(result);
            assertEquals(100, result.getApplicationId());
            verify(applicationRepository, times(1)).save(application);
        }
    }

    @Test
    void testSubmitApplication_UnitNotFound() {
        ApplicationInputDTO inputDto = new ApplicationInputDTO();
        inputDto.setUnitId(99);
        inputDto.setUserId(10);

        when(unitRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UnitIdNotFoundException.class,
                () -> applicationService.submitApplication(inputDto));
    }

    @Test
    void testSubmitApplication_UserNotFound() {
        ApplicationInputDTO inputDto = new ApplicationInputDTO();
        inputDto.setUnitId(1);
        inputDto.setUserId(99);

        Unit unit = new Unit();
        unit.setUnitId(1);

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(UserIdNotFoundException.class,
                () -> applicationService.submitApplication(inputDto));
    }

    @Test
    void testGetApplicationsByUnitId() {
        Application app1 = new Application();
        app1.setApplicationId(100);
        Application app2 = new Application();
        app2.setApplicationId(200);

        ApplicationOutputDTO dto1 = new ApplicationOutputDTO();
        dto1.setApplicationId(100);
        ApplicationOutputDTO dto2 = new ApplicationOutputDTO();
        dto2.setApplicationId(200);

        when(applicationRepository.getApplicationsByUnitId(1)).thenReturn(Arrays.asList(app1, app2));

        try (MockedStatic<ApplicationMapper> mockedMapper = mockStatic(ApplicationMapper.class)) {
            mockedMapper.when(() -> ApplicationMapper.convertToApplicationOutputDto(app1)).thenReturn(dto1);
            mockedMapper.when(() -> ApplicationMapper.convertToApplicationOutputDto(app2)).thenReturn(dto2);

            List<ApplicationOutputDTO> result = applicationService.getApplicationsByUnitId(1);

            assertEquals(2, result.size());
            verify(applicationRepository, times(1)).getApplicationsByUnitId(1);
        }
    }

    @Test
    void testUpdateStatusOfApplication_Success() {
        Application app = new Application();
        app.setApplicationId(100);
        app.setStatus(Application.Status.Pending);

        Application updatedApp = new Application();
        updatedApp.setApplicationId(100);
        updatedApp.setStatus(Application.Status.Approved);

        ApplicationOutputDTO dto = new ApplicationOutputDTO();
        dto.setApplicationId(100);
        dto.setStatus("APPROVED");

        when(applicationRepository.findById(100)).thenReturn(Optional.of(app));
        when(applicationRepository.save(app)).thenReturn(updatedApp);

        try (MockedStatic<ApplicationMapper> mockedMapper = mockStatic(ApplicationMapper.class)) {
            mockedMapper.when(() -> ApplicationMapper.convertToApplicationOutputDto(updatedApp)).thenReturn(dto);

            // updateStatusOfApplication does Application.Status.valueOf(status) with no
            // case conversion, so the argument must match a constant exactly
            // (Pending, Approved, Rejected).
            ApplicationOutputDTO result = applicationService.updateStatusOfApplication(100, "Approved");

            assertEquals("APPROVED", result.getStatus());
            verify(applicationRepository, times(1)).save(app);
        }
    }

    @Test
    void testUpdateStatusOfApplication_NotFound() {
        when(applicationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> applicationService.updateStatusOfApplication(999, "APPROVED"));
    }
}
