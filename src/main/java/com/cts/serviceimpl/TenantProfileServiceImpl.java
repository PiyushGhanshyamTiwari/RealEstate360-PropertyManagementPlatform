package com.cts.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cts.dto.TenantProfileInputDTO;

import com.cts.dto.TenantProfileOutputDTO;

import com.cts.entity.TenantProfile;
import com.cts.entity.User;
import com.cts.exception.TenantIdNotFoundException;
import com.cts.exception.UserIdNotFoundException;
import com.cts.mapper.TenantProfileMapper;
import com.cts.repository.TenantProfileRepository;
import com.cts.repository.UserRepository;
import com.cts.service.TenantProfileService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TenantProfileServiceImpl implements TenantProfileService {

    private final TenantProfileRepository tenantProfileRepository;
    private final UserRepository userRepository;

    @Override
    public TenantProfileOutputDTO addTenant(TenantProfileInputDTO input) {

        
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        
        String folder = "uploads/tenant_docs";
        MultipartFile file = input.getDocumentFileRef();
        String originalName = file.getOriginalFilename();
        String extensionName = originalName.substring(originalName.lastIndexOf("."));
        String fileName = user.getUserId() + "_" +input.getDocumentType()+extensionName;


        try {
            Path path = Paths.get(folder,fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
            file.transferTo(path);
        } catch (IOException e) {
            throw new RuntimeException("File upload failed");
        }

        
        TenantProfile tenantProfile = TenantProfileMapper.convertToTenantProfile(input, user, fileName);

        
        TenantProfile saved = tenantProfileRepository.save(tenantProfile);
        
        
        return TenantProfileMapper.convertToTenantProfileOutputDto(saved);
    }

    @Override
    public List<TenantProfileOutputDTO> getAllTenants() {

        return tenantProfileRepository.findAll()
                .stream()
                .map(TenantProfileMapper::convertToTenantProfileOutputDto)  
                .collect(Collectors.toList());
    }

	
	@Override
	public TenantProfileOutputDTO getTenantById(int tenantId) {

	    TenantProfile tenant = tenantProfileRepository.findById(tenantId)
	            .orElseThrow(() -> new TenantIdNotFoundException("Tenant not found with this id"));

	    return TenantProfileMapper.convertToTenantProfileOutputDto(tenant); 
	}

	@Override
	public TenantProfileOutputDTO getTenantByUserId(int userId) {
		// TODO Auto-generated method stub
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserIdNotFoundException("User Id not found"));

		TenantProfile tenant = tenantProfileRepository.findByUser(user)
				.orElseThrow(() -> new UserIdNotFoundException("Tenant not found with this user Id."));
            

    
    return TenantProfileMapper.convertToTenantProfileOutputDto(tenant);

	}
	

}