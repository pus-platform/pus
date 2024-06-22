package com.ez.pus.controller;

import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Notification;
import com.ez.pus.repository.NotificationRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final UserRepository userRepository;
    private final NotificationRepository repository;
    private final NotificationModelAssembler assembler;

    @Component
    public static class NotificationModelAssembler
            implements RepresentationModelAssembler<Notification, EntityModel<NotificationDTO>> {

        @NonNull
        @Override
        public EntityModel<NotificationDTO> toModel(@NonNull Notification notification) {
            logger.trace("Assembling model for notification: {}", notification.getId());
            return EntityModel.of(NotificationDTO.fromNotification(notification),
                    linkTo(methodOn(NotificationController.class).one(notification.getId())).withSelfRel(),
                    linkTo(methodOn(NotificationController.class).all(0, 3, "notifiedAt")).withRel("notifications"));
        }
    }

    @GetMapping("/{notificationId}")
    EntityModel<NotificationDTO> one(@PathVariable Long notificationId) {
        logger.trace("Fetching notification with ID: {}", notificationId);
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> {
                    logger.error("Notification not found with ID: {}", notificationId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Notification", "ID", notificationId);
                });
        Long user_id = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        if (!Objects.equals(notification.getUser().getId(), user_id)) {
            logger.warn("Notification user ID does not match: {}", user_id);
            throw new CustomExceptionHandling.ResourceNotFoundException("Notification", "ID", notificationId);
        }
        logger.info("Returning notification with ID: {}", notificationId);
        return assembler.toModel(notification);
    }

    @GetMapping
    CollectionModel<EntityModel<NotificationDTO>> all(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "3") int size,
                                                      @RequestParam(defaultValue = "notifiedAt") String sort) {
        Long user_id = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        logger.trace("Listing all notifications for user ID: {}, page: {}, size: {}", user_id, page, size);
        List<Notification> nextPage = repository.findAllByUserId(user_id,
                PageRequest.of(page + 1, size, Sort.by(sort)));
        CollectionModel<EntityModel<NotificationDTO>> notifications = CollectionModel.of(
                        repository.findAllByUserId(user_id, PageRequest.of(page, size, Sort.by(sort).descending())).stream()
                                .map(assembler::toModel)
                                .toList())
                .addIf(page > 0, () -> linkTo(methodOn(NotificationController.class).all(page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(NotificationController.class).all(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(), () -> linkTo(methodOn(NotificationController.class).all(page + 1, size, sort)).withRel("Next Page"));
        if (notifications.getContent().isEmpty()) {
            logger.warn("No notifications found for user ID: {}", user_id);
            return CollectionModel.of(List.of());
        }

        logger.info("Notifications fetched for user ID: {}", user_id);
        return notifications;
    }

    @PostMapping
    ResponseEntity<?> add(@Valid @RequestBody NotificationDTO notificationDTO) {
        logger.debug("Adding new notification");
        Notification notification = Notification.builder()
                .notificationType(notificationDTO.getNotificationType())
                .content(notificationDTO.getContent())
                .build();
        notification.setUser(userRepository.findById(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())).orElseThrow());
        EntityModel<NotificationDTO> entityModel = assembler.toModel(repository.save(notification));
        logger.info("Notification added with ID: {}", notification.getId());
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/{notificationId}")
    ResponseEntity<?> delete(@PathVariable Long notificationId) {
        logger.debug("Deleting notification with ID: {}", notificationId);
        if (!Objects.equals(repository.findById(notificationId)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("Notification", "ID", notificationId))
                .getUser().getUsername(), AuthTokenFilter.getCurrentUserByJwt())) {
            logger.error("You cannot delete notification for another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete notification for another user");
        }
        repository.deleteById(notificationId);
        logger.info("Notification deleted with ID: {}", notificationId);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/notifications")
    public void receiveNotification(NotificationDTO message) {
        logger.debug("Received notification message: {}", message);
    }
}