package com.ez.pus.repository;

import com.ez.pus.model.Post;
import com.ez.pus.model.SharePost;
import com.ez.pus.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharePostRepository extends JpaRepository<SharePost, Long> {
    Optional<SharePost> findByUserAndPost(User user, Post post);
}
