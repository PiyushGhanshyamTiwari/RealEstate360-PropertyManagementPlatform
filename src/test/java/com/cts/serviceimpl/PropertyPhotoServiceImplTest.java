package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.cts.entity.PropertyPhoto;
import com.cts.entity.Unit;
import com.cts.repository.PropertyPhotoRepository;
import com.cts.repository.UnitRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PropertyPhotoServiceImplTest {

    @Mock
    private PropertyPhotoRepository photoRepo;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private PropertyPhotoServiceImpl service;

    private Unit unit;
    private PropertyPhoto photo;

    @BeforeEach
    void setup() {
        unit = new Unit();
        unit.setUnitId(1);

        photo = new PropertyPhoto();
        photo.setPhotoId(10);
        photo.setFileRef("1_.jpg");
    }

     
    @Test
    void testUploadPhotoSuccess() throws IOException {

        MultipartFile file = new MockMultipartFile(
                "file", "image.jpg", "image/jpeg", "data".getBytes()
        );

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));
        when(photoRepo.save(any())).thenReturn(photo);

        PropertyPhoto result =
                service.uploadPhoto(1, file, "caption", "admin");

        assertNotNull(result);
        verify(photoRepo).save(any());
    }

    
    @Test
    void testUploadPhotoIOException() throws IOException {

        MultipartFile file = mock(MultipartFile.class);

        when(file.getOriginalFilename()).thenReturn("image.jpg");

        doThrow(new IOException())
                .when(file)
                .transferTo(any(java.nio.file.Path.class));

        when(unitRepository.findById(1)).thenReturn(Optional.of(unit));

        assertThrows(IOException.class,
                () -> service.uploadPhoto(1, file, "caption", "admin"));
    }

     
    @Test
    void testDownloadPhotoSuccess() throws Exception {

        when(photoRepo.findById(10)).thenReturn(Optional.of(photo));

        Resource resource = service.downloadPhoto(10);

        assertNotNull(resource);
    }

     
    @Test
    void testDownloadPhotoNotFound() {

        when(photoRepo.findById(10)).thenReturn(Optional.empty());

        assertThrows(Exception.class,
                () -> service.downloadPhoto(10));
    }
 
    @Test
    void testPhotosByUnit() {

        when(photoRepo.photosByUnit(1))
                .thenReturn(List.of(photo));

        List<PropertyPhoto> result = service.photosByUnit(1);

        assertEquals(1, result.size());
    }

    
    @Test
    void testPhotosByUnitEmpty() {

        when(photoRepo.photosByUnit(1))
                .thenReturn(List.of());

        List<PropertyPhoto> result = service.photosByUnit(1);

        assertTrue(result.isEmpty());
    }
}
