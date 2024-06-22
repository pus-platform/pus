package com.ez.pus.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ez.pus.model.PostLike;
import com.ez.pus.model.PostLike.PostLikeId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    List<PostLike> findByPostId(Long postId, Pageable pageable);

    @Query("SELECT pl.post.id  " +
            "FROM  post_like pl " +
            "WHERE pl.likedAt >= ?1 " +
            "GROUP BY pl.post.id " +
            "ORDER BY count(*) DESC"
    )
    List<Long> findTrending(LocalDateTime days, Pageable pageable);
}
