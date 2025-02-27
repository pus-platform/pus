package com.ez.pus.repository;

import com.ez.pus.enums.University;
import com.ez.pus.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByUserId(Long userId);

    List<Story> findAllByUserIdIn(List<Long> userIds);

    @Modifying
    @Query("DELETE FROM story s WHERE s.createdAt < :cutoffTime")
    void deleteByCreatedAtBefore(LocalDateTime cutoffTime);
    List<Story> findAllByCommunityName(University community_name);
}
