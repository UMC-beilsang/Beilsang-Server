package com.BeilsangServer.domain.notification.service;

import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.domain.notification.convertor.NotificationConvertor;
import com.BeilsangServer.domain.notification.dto.NotificationResponseDto;
import com.BeilsangServer.domain.notification.entity.AppNotification;
import com.BeilsangServer.domain.notification.repository.AppNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.BeilsangServer.domain.notification.dto.NotificationResponseDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppNotificationService {
    private final MemberRepository memberRepository;
    private final AppNotificationRepository notificationRepository;

    public List<NotificationDto> getNotification(Long memberId) {

        List<NotificationDto> response = new ArrayList<>();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 멤버"));

        // 해당 멤버가 읽지않은 모든 알림 시간순으로 찾기
        List<AppNotification> notifications = notificationRepository.findAllByMemberAndIsReadOrderByCreatedAtDesc(member,false);

        for(AppNotification notification : notifications){
            NotificationDto notificationDto = NotificationConvertor.toNotificationDto(notification);
            response.add(notificationDto);
        }
        return response;
    }
}
