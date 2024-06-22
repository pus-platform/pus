package com.ez.pus.repository;

import com.ez.pus.enums.University;
import com.ez.pus.model.Post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p from post p")
    List<Post> findAllPosts(Pageable pageable);
    List<Post> findAllByIdIn(List<Long> ids);
    List<Post> findByUserId(Long userId, Pageable pageable);
    List<Post> findAllByUserIdIn(List<Long> userIds, Pageable pageable);
    List<Post> findAllByCommunityName(University community, Pageable pageable);
}
