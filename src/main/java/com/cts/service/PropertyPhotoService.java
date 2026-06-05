package com.cts.service;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;   
import org.springframework.web.multipart.MultipartFile;

import com.cts.entity.PropertyPhoto;

public interface PropertyPhotoService {

    public PropertyPhoto uploadPhoto(int unitId, MultipartFile file, String caption, String uploadedBy)
            throws IOException;

    public Resource downloadPhoto(int photoId) throws Exception;

	public List<PropertyPhoto> photosByUnit(int unitID);  

           
}
