package com.BeilsangServer.domain.notification.controller;

import com.BeilsangServer.domain.auth.util.SecurityUtil;
import com.BeilsangServer.domain.notification.dto.NotificationResponseDto.NotificationDto;
import com.BeilsangServer.domain.notification.service.AppNotificationService;
import com.BeilsangServer.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class AppNotificationController {
    private final AppNotificationService appNotificationService;

    //읽음 처리 기능
    @PatchMapping("/{notificationId}")
    @Operation(summary = "알림 읽음 처리 API",
            description = "유저가 알림을 읽음 처리 할때의 API입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<Object> readNotification(@PathVariable(name = "notificationId") Long notificationId){

        appNotificationService.readNotification(notificationId);

        return ApiResponse.onSuccess();
    }

    //읽지 않은 알림 조회 기능
    @GetMapping("")
    @Operation(summary = "알림 전체 조회 API",
            description = "해당 유저가 읽지 않은 모든 알림을 조회하는 API입니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponse<List<NotificationDto>> getNotification(){

        Long memberId = SecurityUtil.getCurrentUserId();

        List<NotificationDto> response = appNotificationService.getNotification(memberId);

        return ApiResponse.onSuccess(response);
    }
}
