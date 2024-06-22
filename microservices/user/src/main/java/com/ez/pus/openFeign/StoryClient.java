package com.ez.pus.openFeign;

import com.ez.pus.dto.StoryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "story-service",
        url = "${application.config.story-url}"
)
public interface StoryClient {

    @GetMapping("/users/{userId}")
    public CollectionModel<EntityModel<StoryDTO>> getStoriesByUserId(@PathVariable Long userId);
}

