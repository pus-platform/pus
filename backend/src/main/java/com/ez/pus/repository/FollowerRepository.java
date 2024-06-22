package com.ez.pus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ez.pus.model.Follower;
import com.ez.pus.model.Follower.FollowerId;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, FollowerId> {
    List<Follower> findAllByFollowedId(Long followedId, Pageable pageable);

    List<Follower> findAllByFollowerId(Long followerId, Pageable pageable);

    List<Follower> findAllByFollowerIdIn(List<Long> followerId);
}
