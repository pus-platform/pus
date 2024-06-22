package com.ez.pus.repository;

import com.ez.pus.model.MajorCommunity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MajorCommunityRepository extends JpaRepository<MajorCommunity, Long> {
    @Query("SELECT m from major_community m")
    List<MajorCommunity> findAllMajors(Pageable pageable);
}
