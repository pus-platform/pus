package com.ez.pus.repository;

import com.ez.pus.enums.University;
import com.ez.pus.model.Community;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    Optional<Community> findByName(@NotNull(message = "Please choose a university") University name);
}
