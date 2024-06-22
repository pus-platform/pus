package com.ez.pus.openFeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "upload-service",
        url = "${application.config.upload-url}"
)
public interface UploadClient {

    @PostMapping(path = "/", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    String handleFileUpload(MultipartFile file);
}
