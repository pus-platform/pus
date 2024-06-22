package com.ez.pus.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ez.pus.model.StoryLike;
import com.ez.pus.model.StoryLike.StoryLikeId;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryLikeRepository extends JpaRepository<StoryLike, StoryLikeId> {
    List<StoryLike> findByStoryId(Long storyId, Pageable pageable);
}

