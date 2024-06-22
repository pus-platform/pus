package com.ez.pus.bookmark;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Bookmark.BookmarkId> {

    @Query("SELECT b FROM bookmark b WHERE b.userId = :user_id AND b.post.id = :post_id")
    Optional<Bookmark> findByUserIdAndPostId(Long user_id, Long post_id);

    @Query("SELECT b FROM bookmark b WHERE b.userId = :user_id")
    List<Bookmark> findAllByUserId(Long user_id, Pageable pageable);
}
