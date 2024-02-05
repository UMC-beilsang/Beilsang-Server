package com.BeilsangServer.domain.notification.repository;

import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.notification.entity.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findAllByMemberAndIsReadOrderByCreatedAtDesc(Member member, Boolean isRead);
}
