package com.codingShuttle.linkedInProject.uploaderService.controller;

import com.codingShuttle.linkedInProject.uploaderService.service.CloudinaryUploaderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class UploaderController {

    private final CloudinaryUploaderService cloudinaryUploaderService;

    public UploaderController(CloudinaryUploaderService cloudinaryUploaderService) {
        this.cloudinaryUploaderService = cloudinaryUploaderService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
       String url = cloudinaryUploaderService.upload(file);
       return ResponseEntity.ok(url);
    }

}
