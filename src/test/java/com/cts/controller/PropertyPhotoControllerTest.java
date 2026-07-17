package com.cts.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.cts.entity.PropertyPhoto;
import com.cts.service.PropertyPhotoService;

@ExtendWith(MockitoExtension.class)
class PropertyPhotoControllerTest {

    @Mock
    private PropertyPhotoService service;

    @InjectMocks
    private PropertyPhotoController controller;

    @Test
    void testUploadPhoto() throws Exception {

        int unitId = 1;
        String caption = "Front View";
        String uploadedBy = "John";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "test-image".getBytes());

        PropertyPhoto photo = new PropertyPhoto();

        when(service.uploadPhoto(unitId, file, caption, uploadedBy))
                .thenReturn(photo);

        ResponseEntity<PropertyPhoto> response =
                controller.uploadPhoto(unitId, file, caption, uploadedBy);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(photo, response.getBody());

        verify(service).uploadPhoto(unitId, file, caption, uploadedBy);
    }

    @Test
    void testDownloadPhoto() throws Exception {

        int photoId = 101;

        Resource resource =
                new ByteArrayResource("dummy-content".getBytes()) {
                    @Override
                    public String getFilename() {
                        return "photo.jpg";
                    }
                };

        when(service.downloadPhoto(photoId))
                .thenReturn(resource);

        ResponseEntity<?> response =
                controller.downloadPhoto(photoId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(resource, response.getBody());

        assertEquals(
                "application/octet-stream",
                response.getHeaders().getContentType().toString());

        verify(service).downloadPhoto(photoId);
    }

    @Test
    void testViewPhoto() throws Exception {

        int photoId = 100;
        byte[] imageBytes = "image-data".getBytes();

        when(service.getImageBinary(photoId))
                .thenReturn(imageBytes);

        ResponseEntity<byte[]> response =
                controller.viewPhoto(photoId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(imageBytes, response.getBody());

        assertEquals(
                "image/jpeg",
                response.getHeaders().getContentType().toString());

        verify(service).getImageBinary(photoId);
    }
}