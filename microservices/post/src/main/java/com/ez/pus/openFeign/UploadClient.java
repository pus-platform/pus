package com.ez.pus.openFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@FeignClient(name = "upload-service", url = "${application.config.upload-url}")
public interface UploadClient {

    @PostMapping(path = "/", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    String handleFileUpload(MultipartFile file);
}
