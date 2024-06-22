package com.ez.pus.repository;

import com.ez.pus.enums.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ez.pus.model.CommentLike;
import com.ez.pus.model.CommentLike.CommentLikeId;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    List<CommentLike> findAllByCommentId(Long comment_id, Pageable pageable);

    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);

    List<CommentLike> findAllByCommentIdAndReaction(Long comment_id, Reaction reaction, Pageable pageable);
}
