package com.ez.pus.replyToStory;

import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UserClient;
import com.ez.pus.story.StoryRepository;
import com.ez.pus.utils.EncryptionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stories")
public class ReplyToStoryController {

    private static final Logger logger = LoggerFactory.getLogger(ReplyToStoryController.class);
    private final UserClient userClient;
    private final ReplyToStoryRepository replyToStoryRepository;
    private final StoryRepository storyRepository;
    private final ReplyToStoryModelAssembler assembler;


    @GetMapping("/{storyId}/replies")
    public CollectionModel<EntityModel<ReplyToStoryDTO>> getRepliesToStory(@PathVariable Long storyId) {
        logger.trace("Fetching replies for story with ID: {}", storyId);
        if (!Objects.equals(storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId))
                .getUserId(), userClient.getCurrentUser().getId())) {
            logger.error("You cannot view replies of a story that's not yours");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot view replies of a story that's not yours");
        }
        List<EntityModel<ReplyToStoryDTO>> replies = replyToStoryRepository.findByStoryId(storyId).stream()
                .map(reply -> {
                    String content = reply.getReplyContent();
                    try {
                        Base64.getDecoder().decode(content);
                        reply.setReplyContent(EncryptionUtil.decrypt(content));
                    } catch (IllegalArgumentException e) {
                        reply.setReplyContent(content);
                    }
                    return assembler.toModel(reply);
                })
                .collect(Collectors.toList());
        if (replies.isEmpty()) {
            logger.warn("No replies found for story with ID: {}", storyId);
            throw new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
        }
        logger.info("Fetched {} replies for story with ID: {}", replies.size(), storyId);
        return CollectionModel.of(replies);
    }

    @GetMapping("/{storyId}/replies/{replyId}")
    public EntityModel<ReplyToStoryDTO> getReply(@PathVariable Long replyId) {
        logger.trace("Fetching reply with ID: {}", replyId);
        ReplyToStory reply = replyToStoryRepository.findById(replyId)
                .orElseThrow(() -> {
                    logger.error("Failed to find reply with ID: {}", replyId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Reply", "ID", replyId);
                });
        if (!Objects.equals(userClient.getCurrentUser().getId(), reply.getUserId()) &&
                !Objects.equals(userClient.getCurrentUser().getId(), reply.getStory().getUserId())) {
            logger.error("cannot view story reply of another user");
            throw new CustomExceptionHandling.InvalidArgumentException("cannot view story reply of another user");
        }
        String content = reply.getReplyContent();
        try {
            Base64.getDecoder().decode(content);
            reply.setReplyContent(EncryptionUtil.decrypt(content));
        } catch (IllegalArgumentException e) {
            reply.setReplyContent(content);
        }
        logger.info("Successfully fetched reply with ID: {}", replyId);
        return assembler.toModel(reply);
    }

    @PutMapping("/{storyId}/replies/{replyId}")
    public ResponseEntity<EntityModel<ReplyToStoryDTO>> updateReply(@PathVariable Long replyId,
                                                                    @Valid @RequestBody ReplyToStoryDTO newReplyDTO) {
        logger.trace("Updating reply with ID: {}", replyId);
        ReplyToStory updatedReply = replyToStoryRepository.findById(replyId)
                .map(reply -> {
                    if (!Objects.equals(userClient.getCurrentUser().getId(), reply.getUserId())) {
                        logger.error("cannot edit story reply of another user");
                        throw new CustomExceptionHandling.InvalidArgumentException("cannot edit story reply of another user");
                    }
                    reply.setReplyContent(newReplyDTO.getReplyContent());
                    return replyToStoryRepository.save(reply);
                })
                .orElseThrow(() -> {
                    logger.error("Failed to find reply with ID: {}", replyId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Reply", "ID", replyId);
                });
        logger.info("Successfully updated reply with ID: {}", replyId);
        return ResponseEntity.created(linkTo(methodOn(ReplyToStoryController.class)
                        .getReply(updatedReply.getId())).withSelfRel().toUri())
                .body(assembler.toModel(updatedReply));
    }

    @DeleteMapping("/{storyId}/replies/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable Long replyId) {
        logger.trace("Attempting to delete reply with ID: {}", replyId);
        replyToStoryRepository.findById(replyId)
                .ifPresentOrElse(reply -> {
                    if (!Objects.equals(userClient.getCurrentUser().getId(), reply.getUserId())) {
                        logger.error("cannot delete story reply of another user");
                        throw new CustomExceptionHandling.InvalidArgumentException("cannot delete story reply of another user");
                    }
                    replyToStoryRepository.delete(reply);
                    logger.info("Successfully deleted reply with ID: {}", replyId);
                }, () -> {
                    logger.error("Failed to find reply with ID: {} for deletion", replyId);
                    throw new CustomExceptionHandling.ResourceNotFoundException("Reply", "ID", replyId);
                });
        return ResponseEntity.noContent().build();
    }
}
