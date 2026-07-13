package com.cts.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cts.entity.PropertyPhoto;
import com.cts.entity.Unit;
import com.cts.repository.PropertyPhotoRepository;
import com.cts.repository.UnitRepository;
import com.cts.service.PropertyPhotoService;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class PropertyPhotoServiceImpl implements PropertyPhotoService {

    private final PropertyPhotoRepository photoRepo;
    private final UnitRepository unitRepository;

    
    @Override
    public PropertyPhoto uploadPhoto(int unitId, MultipartFile file, String caption, String uploadedBy)
            throws IOException {

        Unit unit = unitRepository.findById(unitId).orElse(null);
        PropertyPhoto propertyPhoto = new PropertyPhoto();
        String folder = "uploads/property-photos";
        String extensionName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = unit.getUnitId() + "_" + java.util.UUID.randomUUID().toString() + extensionName;

        Path path = Paths.get(folder, fileName);

        Files.createDirectories(path.getParent());

        file.transferTo(path);

        PropertyPhoto photo = new PropertyPhoto();

        photo.setUnit(unit);
        photo.setFileRef(fileName);
        photo.setCaption(caption);
        photo.setUploadedBy(uploadedBy);
        

        return photoRepo.save(photo);
    }

    
    @Override
    public Resource downloadPhoto(int photoId) throws Exception {

        PropertyPhoto photo = photoRepo.findById(photoId).get();

        Path path = Paths.get("uploads/property-photos", photo.getFileRef());

        return new UrlResource(path.toUri());
    }

    @Override
    public byte[] getImageBinary(int photoId) throws Exception {
        PropertyPhoto photo = photoRepo.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found with id: " + photoId));

        Path path = Paths.get("uploads/property-photos", photo.getFileRef());

        // Reads the file content into a byte array
        return Files.readAllBytes(path);
    }

    @Override
    public HashMap<Integer, String> photosByUnit(int unitID) {

        Unit unit = unitRepository.findById(unitID).orElse(null);

        if (unit == null) {
            return new HashMap<>();
        }

        List<PropertyPhoto> list = photoRepo.photosByUnit(unitID);

        HashMap<Integer, String> photoMap = new HashMap<>();

        for (PropertyPhoto photo : list) {
            photoMap.put(photo.getPhotoId(), photo.getCaption());
        }

        return photoMap;
    }
    

}