package com.cts.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
import com.cts.service.ApplicationService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private ApplicationRepository applicationRepository;
    private UnitRepository unitRepository;
    private UserRepository userRepository;
	@Override
	public ApplicationOutputDTO submitApplication(ApplicationInputDTO input) {
		// TODO Auto-generated method stub
		
		Unit unit = unitRepository.findById(input.getUnitId())
				.orElseThrow(()->new UnitIdNotFoundException("UnitId not found"));
		User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new UserIdNotFoundException("UserId not found"));
		
        Application application = ApplicationMapper.convertToApllication(input,unit,user);
        
        Application savedApplication = applicationRepository.save(application);

        return ApplicationMapper.convertToApplicationOutputDto(savedApplication);
	}
	@Override
	public List<ApplicationOutputDTO> getApplicationsByUnitId(int unitId) {
		// TODO Auto-generated method stub
		return applicationRepository.getApplicationsByUnitId(unitId)
				.stream()
				.map(ApplicationMapper::convertToApplicationOutputDto)
				.collect(Collectors.toList());
				
	}
	@Override
	public ApplicationOutputDTO updateStatusOfApplication(int applicationId, String status) {

	    Application app = applicationRepository.findById(applicationId)
	            .orElseThrow(() -> new RuntimeException("Application not found"));

	    Application.Status enumStatus = Application.Status.valueOf(status);

	    app.setStatus(enumStatus);

	    Application updatedApp = applicationRepository.save(app);

	    return ApplicationMapper.convertToApplicationOutputDto(updatedApp);
	}
	
	
	

    

	
}
