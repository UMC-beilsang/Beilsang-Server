package com.BeilsangServer.domain.notification.controller;

import com.BeilsangServer.domain.notification.dto.NotificationResponseDto.NotificationDto;
import com.BeilsangServer.domain.notification.service.AppNotificationService;
import com.BeilsangServer.global.common.apiResponse.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.BeilsangServer.global.common.apiResponse.ApiResponseStatus.REQUEST_SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class AppNotificationController {
    private final AppNotificationService appNotificationService;

    //읽음 처리 기능

    //읽지 않은 알림 조회 기능
    @GetMapping("/{memberId}")
    @Operation(summary = "알림 전체 조회 API", description = "해당 유저가 읽지 않은 모든 알림을 조회하는 API입니다.")
    public ApiResponse<List<NotificationDto>> getNotification(@PathVariable(name = "memberId") Long memberId){
        // 로그인 구현후 토큰에서 멤버Id 추출하는것으로 변경

        List<NotificationDto> response = appNotificationService.getNotification(memberId);

        return new ApiResponse<>(REQUEST_SUCCESS, response);
    }
}
