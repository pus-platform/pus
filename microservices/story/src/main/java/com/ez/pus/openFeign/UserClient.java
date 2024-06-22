package com.ez.pus.openFeign;

import com.ez.pus.dto.FollowerDTO;
import com.ez.pus.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service",
        url = "${application.config.user-url}"
)
public interface UserClient {
    @GetMapping("/{userId}")
    EntityModel<UserDTO> one(@PathVariable Long userId);

    @GetMapping("/current-user")
    UserDTO getCurrentUser();

    @GetMapping("/{userId}/following")
    CollectionModel<EntityModel<FollowerDTO>> allFollowing(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "3") int size,
                                                                  @RequestParam(defaultValue = "followedAt") String sort);
    @GetMapping("/{userId}")
    UserDTO getUserById(@PathVariable Long userId);
}
