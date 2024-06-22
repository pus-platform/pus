package com.ez.pus.user;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("Select u.id from User u where u.username = ?1")
    Long findIdByUsername(String Username);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsersByUsernameOrFullname(@Param("searchTerm") String searchTerm);

    @Query("SELECT u from User u")
    List<User> findAllUsers(Pageable pageable);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findAllByIdIn(Set<Long> ids);
}
