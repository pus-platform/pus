package com.ez.pus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ez.pus.model.ReplyToStory;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyToStoryRepository extends JpaRepository<ReplyToStory, Long> {
    List<ReplyToStory> findByStoryId(Long storyId);
}
