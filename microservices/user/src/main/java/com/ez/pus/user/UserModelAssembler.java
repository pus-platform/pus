package com.ez.pus.user;

import com.ez.pus.follower.FollowerController;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<UserDTO>> {

        @NonNull
        @Override
        public EntityModel<UserDTO> toModel(@NonNull User user) {
                return EntityModel.of(UserDTO.fromUser(user),
                                linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
                                linkTo(methodOn(UserController.class).all(0, 3, "id")).withRel("users"),
                                linkTo(methodOn(FollowerController.class).allFollowers(user.getId(), 0, 3,
                                                "followedAt")).withRel("followers"));
        }
}