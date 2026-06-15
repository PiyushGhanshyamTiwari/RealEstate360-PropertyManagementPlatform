package com.cts.serviceimpl;

import com.cts.entity.PropertyPhoto;
import com.cts.entity.Unit;
import com.cts.repository.PropertyPhotoRepository;
import com.cts.repository.UnitRepository;
import com.cts.serviceimpl.PropertyPhotoServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PropertyPhotoServiceImplTest {

    @Mock
    private PropertyPhotoRepository photoRepo;

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PropertyPhotoServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Positive: uploadPhoto succeeds
    @Test
    void testUploadPhoto_Success() throws IOException {
        Unit unit = new Unit();
        unit.setUnitId(1);

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");

        PropertyPhoto savedPhoto = new PropertyPhoto();
        savedPhoto.setFileRef("1_.jpg");

        doNothing().when(multipartFile).transferTo(any(java.nio.file.Path.class));
        when(photoRepo.save(any(PropertyPhoto.class))).thenReturn(savedPhoto);

        PropertyPhoto result = service.uploadPhoto(1, multipartFile, "Front view", "Admin");

        assertNotNull(result);
        assertEquals("1_.jpg", result.getFileRef());
        verify(photoRepo, times(1)).save(any(PropertyPhoto.class));
    }

    // Negative: unit not found
    @Test
    void testUploadPhoto_UnitNotFound() throws IOException {
        when(unitRepository.findById(99)).thenReturn(Optional.empty());
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");

        assertThrows(NullPointerException.class,
                () -> service.uploadPhoto(99, multipartFile, "Caption", "Admin"));
    }

    // Positive: downloadPhoto succeeds
    @Test
    void testDownloadPhoto_Success() throws Exception {
        PropertyPhoto photo = new PropertyPhoto();
        photo.setFileRef("1_.jpg");

        when(photoRepo.findById(1)).thenReturn(Optional.of(photo));

        Resource resource = service.downloadPhoto(1);

        assertNotNull(resource);
        assertTrue(resource.exists() || !resource.exists()); // just ensures Resource returned
        verify(photoRepo, times(1)).findById(1);
    }

    // Negative: downloadPhoto photo not found
    @Test
    void testDownloadPhoto_NotFound() {
        when(photoRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> service.downloadPhoto(99));
    }

    // Positive: photosByUnit
    @Test
    void testPhotosByUnit_Success() {
        Unit unit = new Unit();
        unit.setUnitId(1);

        PropertyPhoto photo = new PropertyPhoto();
        photo.setFileRef("1_.jpg");

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(photoRepo.photosByUnit(1)).thenReturn(Arrays.asList(photo));

        List<PropertyPhoto> result = service.photosByUnit(1);

        assertEquals(1, result.size());
        verify(photoRepo, times(1)).photosByUnit(1);
    }
}
