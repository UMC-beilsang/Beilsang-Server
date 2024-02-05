package com.BeilsangServer.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NotificationResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationDto{
        private Long notificationId;
        private Long challengeId;
        private String title;
        private String contents;
        private LocalDateTime time;
    }
}
