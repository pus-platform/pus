package com.ez.pus.repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.ez.pus.model.Viewer;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewerRepository extends JpaRepository<Viewer, Viewer.ViewerId> {
    List<Viewer> findViewersByStoryId(@Param("storyId") Long storyId, Pageable pageable);
}
