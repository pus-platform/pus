package com.ez.pus.repository;

import com.ez.pus.enums.NotificationType;
import com.ez.pus.model.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserId(Long userId, Pageable pageable);

    List<Notification> findAllByUserIdAndNotificationType(Long user_id, NotificationType type);
}
