package com.ez.pus.repository;

import com.ez.pus.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllBySenderId(Long sender_id);
    List<Message> findAllByReceiverUserId(Long receiver_id);
    List<Message> findAllByReceiverUserIdAndSenderId(Long receiver_id, Long sender_id);
}
