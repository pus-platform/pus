package com.ez.pus.viewer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewerRepository extends JpaRepository<Viewer, Viewer.ViewerId> {
    List<Viewer> findViewersByStoryId(@Param("storyId") Long storyId, Pageable pageable);
}
