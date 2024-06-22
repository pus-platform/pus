package com.ez.pus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ez.pus.model.GroupChat;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {
}
