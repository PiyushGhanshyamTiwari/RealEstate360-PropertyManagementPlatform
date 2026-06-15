package com.cts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.cts.entity.PropertyPhoto;
import com.cts.serviceimpl.PropertyPhotoServiceImpl;

/**
 * Unit tests for {@link PropertyPhotoController}.
 *
 * <p>Mocks the concrete {@link PropertyPhotoServiceImpl} and injects it into the controller.
 * Exercises multipart upload, binary download (verifying the {@code Content-Disposition} header and
 * streamed bytes), list retrieval, the required {@code uploadedBy} parameter, and surfacing of
 * unmapped service failures.
 */
@ExtendWith(MockitoExtension.class)
class PropertyPhotoControllerTest extends AbstractControllerTest {

    @Mock
    private PropertyPhotoServiceImpl service;

    @InjectMocks
    private PropertyPhotoController propertyPhotoController;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/propertyphoto";
    private static final byte[] FILE_BYTES = "fake-image-bytes".getBytes();

    @BeforeEach
    void setUp() {
        mockMvc = buildMockMvc(propertyPhotoController);
    }

    private PropertyPhoto samplePhoto() {
        return PropertyPhoto.builder()
                .photoId(1)
                .fileRef("uploads/photo.png")
                .caption("Front view")
                .uploadedBy("admin")
                .uploadedAt(LocalDateTime.now())
                .build();
    }

    private MockMultipartFile multipartImage() {
        return new MockMultipartFile("file", "photo.png", MediaType.IMAGE_PNG_VALUE, FILE_BYTES);
    }

    @Test
    @DisplayName("POST upload photo -> 201 with stored photo")
    void shouldReturn201WhenPhotoUploaded() throws Exception {
        // Arrange
        when(service.uploadPhoto(eq(10), any(MultipartFile.class), eq("Front view"), eq("admin")))
                .thenReturn(samplePhoto());

        // Act & Assert
        mockMvc.perform(multipart(BASE_URL + "/upload/{unitId}", 10)
                        .file(multipartImage())
                        .param("caption", "Front view")
                        .param("uploadedBy", "admin"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.photoId").value(1))
                .andExpect(jsonPath("$.caption").value("Front view"))
                .andExpect(jsonPath("$.uploadedBy").value("admin"));

        verify(service, times(1))
                .uploadPhoto(eq(10), any(MultipartFile.class), eq("Front view"), eq("admin"));
    }

    @Test
    @DisplayName("POST upload photo without required uploadedBy -> 400 and service not invoked")
    void shouldReturn400WhenUploadedByMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(multipart(BASE_URL + "/upload/{unitId}", 10)
                        .file(multipartImage())
                        .param("caption", "Front view"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST upload photo when service fails -> failure surfaced")
    void shouldSurfaceFailureWhenUploadFails() throws Exception {
        // Arrange
        when(service.uploadPhoto(eq(10), any(MultipartFile.class), any(), eq("admin")))
                .thenThrow(new RuntimeException("File upload failed"));

        // Act & Assert
        assertRequestFailsWith("File upload failed", () ->
                mockMvc.perform(multipart(BASE_URL + "/upload/{unitId}", 10)
                        .file(multipartImage())
                        .param("uploadedBy", "admin")));

        verify(service, times(1))
                .uploadPhoto(eq(10), any(MultipartFile.class), any(), eq("admin"));
    }

    @Test
    @DisplayName("GET photos by unit -> 200 with list")
    void shouldReturn200WhenPhotosExist() throws Exception {
        // Arrange
        when(service.photosByUnit(10)).thenReturn(List.of(samplePhoto()));

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unitID/{unitID}", 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].photoId").value(1))
                .andExpect(jsonPath("$[0].uploadedBy").value("admin"));

        verify(service, times(1)).photosByUnit(10);
    }

    @Test
    @DisplayName("GET photos by unit when none -> 200 with empty list")
    void shouldReturn200WithEmptyListWhenNoPhotos() throws Exception {
        // Arrange
        when(service.photosByUnit(99)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/unitID/{unitID}", 99))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(service, times(1)).photosByUnit(99);
    }

    @Test
    @DisplayName("GET download photo -> 200 with octet-stream attachment")
    void shouldReturn200WhenPhotoDownloaded() throws Exception {
        // Arrange
        Resource resource = new ByteArrayResource(FILE_BYTES) {
            @Override
            public String getFilename() {
                return "photo.png";
            }
        };
        when(service.downloadPhoto(1)).thenReturn(resource);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/download/{photoId}", 1))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("attachment")))
                .andExpect(content().bytes(FILE_BYTES));

        verify(service, times(1)).downloadPhoto(1);
    }

    @Test
    @DisplayName("GET download photo when service fails -> failure surfaced")
    void shouldSurfaceFailureWhenDownloadFails() throws Exception {
        // Arrange
        when(service.downloadPhoto(99)).thenThrow(new RuntimeException("Photo not found"));

        // Act & Assert
        assertRequestFailsWith("Photo not found", () ->
                mockMvc.perform(get(BASE_URL + "/download/{photoId}", 99)));

        verify(service, times(1)).downloadPhoto(99);
    }

    @Test
    @DisplayName("GET download photo with non-numeric id -> 400 and service not invoked")
    void shouldReturn400WhenPhotoIdNotNumeric() throws Exception {
        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/download/{photoId}", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
