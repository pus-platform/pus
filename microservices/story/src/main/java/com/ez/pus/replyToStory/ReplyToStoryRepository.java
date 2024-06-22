package com.ez.pus.replyToStory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReplyToStoryRepository extends JpaRepository<ReplyToStory, Long> {
    List<ReplyToStory> findByStoryId(Long storyId);
}
