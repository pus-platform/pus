package com.ez.pus.follower;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Follower.FollowerId> {
    List<Follower> findAllByFollowedId(Long followedId, Pageable pageable);

    List<Follower> findAllByFollowerId(Long followerId, Pageable pageable);

    List<Follower> findAllByFollowerIdIn(List<Long> followerId);
}
