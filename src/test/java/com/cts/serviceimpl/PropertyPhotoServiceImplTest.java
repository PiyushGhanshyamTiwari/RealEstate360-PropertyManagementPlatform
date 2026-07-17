package com.cts.serviceimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.cts.entity.PropertyPhoto;
import com.cts.entity.Unit;
import com.cts.repository.PropertyPhotoRepository;
import com.cts.repository.UnitRepository;

@ExtendWith(MockitoExtension.class)
class PropertyPhotoServiceImplTest {

    @Mock
    private PropertyPhotoRepository photoRepo;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private PropertyPhotoServiceImpl service;

    @Test
    void testUploadPhoto() throws Exception {

        Unit unit = new Unit();
        unit.setUnitId(1);

        MultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "sample-image".getBytes());

        PropertyPhoto savedPhoto = new PropertyPhoto();

        when(unitRepository.findById(1))
                .thenReturn(Optional.of(unit));

        when(photoRepo.save(any(PropertyPhoto.class)))
                .thenReturn(savedPhoto);

        PropertyPhoto result =
                service.uploadPhoto(1, file, "Front View", "Admin");

        assertNotNull(result);

        verify(unitRepository).findById(1);
        verify(photoRepo).save(any(PropertyPhoto.class));
    }

    @Test
    void testDownloadPhoto() throws Exception {

        PropertyPhoto photo = new PropertyPhoto();
        photo.setFileRef("test.jpg");

        when(photoRepo.findById(1))
                .thenReturn(Optional.of(photo));

        Resource resource = service.downloadPhoto(1);

        assertNotNull(resource);
        verify(photoRepo).findById(1);
    }

    @Test
    void testGetImageBinary() throws Exception {

        Files.createDirectories(Path.of("uploads/property-photos"));

        Path filePath =
                Path.of("uploads/property-photos/test.jpg");

        byte[] expected = "image-content".getBytes();

        Files.write(filePath, expected);

        PropertyPhoto photo = new PropertyPhoto();
        photo.setFileRef("test.jpg");

        when(photoRepo.findById(1))
                .thenReturn(Optional.of(photo));

        byte[] result = service.getImageBinary(1);

        assertArrayEquals(expected, result);
    }

    @Test
    void testGetImageBinaryPhotoNotFound() {

        when(photoRepo.findById(1))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> service.getImageBinary(1));

        assertEquals(
                "Photo not found with id: 1",
                exception.getMessage());
    }

    @Test
    void testPhotosByUnitWhenUnitExists() {

        Unit unit = new Unit();
        unit.setUnitId(1);

        PropertyPhoto photo1 = new PropertyPhoto();
        photo1.setPhotoId(101);
        photo1.setCaption("Living Room");

        PropertyPhoto photo2 = new PropertyPhoto();
        photo2.setPhotoId(102);
        photo2.setCaption("Bedroom");

        when(unitRepository.findById(1))
                .thenReturn(Optional.of(unit));

        when(photoRepo.photosByUnit(1))
                .thenReturn(List.of(photo1, photo2));

        HashMap<Integer, String> result =
                service.photosByUnit(1);

        assertEquals(2, result.size());
        assertEquals("Living Room", result.get(101));
        assertEquals("Bedroom", result.get(102));
    }

    @Test
    void testPhotosByUnitWhenUnitNotFound() {

        when(unitRepository.findById(1))
                .thenReturn(Optional.empty());

        HashMap<Integer, String> result =
                service.photosByUnit(1);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(photoRepo, never()).photosByUnit(anyInt());
    }
}