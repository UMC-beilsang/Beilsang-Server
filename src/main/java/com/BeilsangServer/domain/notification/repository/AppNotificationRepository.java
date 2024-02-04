package com.BeilsangServer.domain.notification.repository;

import com.BeilsangServer.domain.notification.entity.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
}
