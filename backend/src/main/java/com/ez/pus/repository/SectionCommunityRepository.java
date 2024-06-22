package com.ez.pus.repository;

import com.ez.pus.model.SectionCommunity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionCommunityRepository extends JpaRepository<SectionCommunity, Long> {
    @Query("SELECT s from section_community s")
    List<SectionCommunity> findAllSections(Pageable pageable);
}
