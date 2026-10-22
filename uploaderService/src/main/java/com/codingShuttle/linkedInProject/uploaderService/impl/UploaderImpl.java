package com.codingShuttle.linkedInProject.uploaderService.impl;

import org.springframework.web.multipart.MultipartFile;

public interface UploaderImpl {

    String upload(MultipartFile multipartFile);
}
