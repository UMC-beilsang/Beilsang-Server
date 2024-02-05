package com.BeilsangServer.domain.notification.convertor;

import com.BeilsangServer.domain.notification.dto.NotificationResponseDto;
import com.BeilsangServer.domain.notification.entity.AppNotification;
import com.BeilsangServer.domain.notification.entity.ChallengeNotification;

public class NotificationConvertor {

    public static NotificationResponseDto.NotificationDto toNotificationDto(AppNotification appNotification) {
        if (appNotification instanceof ChallengeNotification) {
            ChallengeNotification challengeNotification = (ChallengeNotification) appNotification;

            return NotificationResponseDto.NotificationDto.builder()
                    .notificationId(appNotification.getId())
                    .challengeId(challengeNotification.getChallengeId())
                    .title(appNotification.getTitle())
                    .contents(appNotification.getContents())
                    .time(appNotification.getCreatedAt())
                    .build();
        }
        return NotificationResponseDto.NotificationDto.builder()
                .notificationId(appNotification.getId())
                .title(appNotification.getTitle())
                .contents(appNotification.getContents())
                .time(appNotification.getCreatedAt())
                .build();
    }
}
