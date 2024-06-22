package com.ez.pus.storyLike;

import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StoryLikeRepository extends JpaRepository<StoryLike, StoryLike.StoryLikeId> {
    List<StoryLike> findByStoryId(Long storyId, Pageable pageable);
}

