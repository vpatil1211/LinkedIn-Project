package com.codingShuttle.linkedInProject.uploaderService.service;

import com.cloudinary.Cloudinary;
import com.codingShuttle.linkedInProject.uploaderService.exception.BadRequestException;
import com.codingShuttle.linkedInProject.uploaderService.impl.UploaderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryUploaderService  implements UploaderImpl {

    private final Cloudinary cloudinary;

    public CloudinaryUploaderService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new BadRequestException("Unable To upload file! please try later");
        }
    }
}
