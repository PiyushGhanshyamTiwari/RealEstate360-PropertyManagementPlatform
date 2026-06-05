package com.cts.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cts.entity.PropertyPhoto;
import com.cts.repository.PropertyPhotoRepository;
import com.cts.service.PropertyPhotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/propertyphoto")
@Tag(name = "Property Photo Controller",description = "Operation related to uploading and downloading photos of unit")
public class PropertyPhotoController {

    private final PropertyPhotoService service;

   
    @PostMapping(value = "/upload/{unitId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Photos of Units")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PropertyPhoto> uploadPhoto(
            @PathVariable int unitId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String caption,
            @RequestParam String uploadedBy) throws Exception {

        PropertyPhoto photo = service.uploadPhoto(unitId, file, caption, uploadedBy);

        return new ResponseEntity<>(photo, HttpStatus.CREATED);
    }

    
    @GetMapping("/unitID/{unitID}")
    @Operation(summary = "Return list of photos of units")
    @PreAuthorize("hasAnyRole('OWNER','TENANT')")
    public ResponseEntity<?> photosbyUnit(@PathVariable int unitID){

        List<PropertyPhoto> list = service.photosByUnit(unitID);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    
    
    @GetMapping("/download/{photoId}")
    @Operation(summary = "Download photos of units")
    @PreAuthorize("hasAnyRole('OWNER','TENANT')")
    public ResponseEntity<?> downloadPhoto(@PathVariable int photoId) throws Exception {

        Resource file = (Resource) service.downloadPhoto(photoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", file.getFilename());

        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

  
}