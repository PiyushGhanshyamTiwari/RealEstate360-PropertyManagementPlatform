package com.cts.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.cts.entity.PropertyPhoto;
import com.cts.service.PropertyPhotoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class PropertyPhotoControllerTest {

    @Mock
    private PropertyPhotoService service;

    @InjectMocks
    private PropertyPhotoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    @Test
    void testUploadPhoto() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "data".getBytes()
        );

        PropertyPhoto photo = new PropertyPhoto();
        photo.setFileRef("image.jpg");

        when(service.uploadPhoto(eq(1), any(), eq("caption"), eq("admin")))
                .thenReturn(photo);

        mockMvc.perform(multipart("/api/v1/propertyphoto/upload/{unitId}", 1)
                        .file(file)
                        .param("caption", "caption")
                        .param("uploadedBy", "admin"))
                .andExpect(status().isCreated());

        verify(service).uploadPhoto(eq(1), any(), eq("caption"), eq("admin"));
    }

    
    @Test
    void testPhotosByUnit() throws Exception {

        when(service.photosByUnit(1))
                .thenReturn(List.of(new PropertyPhoto()));

        mockMvc.perform(get("/api/v1/propertyphoto/unitID/{unitID}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(service).photosByUnit(1);
    }

    
    @Test
    void testPhotosByUnitEmpty() throws Exception {

        when(service.photosByUnit(1))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/propertyphoto/unitID/{unitID}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    void testDownloadPhoto() throws Exception {

        Resource resource = new ByteArrayResource("data".getBytes()) {
            @Override
            public String getFilename() {
                return "file.jpg";
            }
        };

        when(service.downloadPhoto(1)).thenReturn(resource);

        mockMvc.perform(get("/api/v1/propertyphoto/download/{photoId}", 1))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/octet-stream"))
                .andExpect(header().string("Content-Disposition",
                        "form-data; name=\"attachment\"; filename=\"file.jpg\""));

        verify(service).downloadPhoto(1);
    }
}