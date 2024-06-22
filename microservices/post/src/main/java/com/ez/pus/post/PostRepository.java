package com.ez.pus.post;

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
    List<Post> findByUser(Long userId, Pageable pageable);
    List<Post> findAllByUserIn(List<Long> userIds, Pageable pageable);
}
