package com.ez.pus.openFeign;

import com.ez.pus.dto.PostDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "post-service",
        url = "${application.config.post-url}"
)
public interface PostClient {

    @GetMapping("/user")
    public CollectionModel<EntityModel<PostDTO>> getAllPostsByUserId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "createdAt") String sort);
}

