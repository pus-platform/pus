package com.ez.pus.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId, Pageable pageable);

    List<Comment> findAllByRepliedCommentId(Long comment, Pageable pageable);
}
