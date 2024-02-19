package com.BeilsangServer.domain.auth.controller;


import com.BeilsangServer.domain.auth.dto.KakaoRequestDto;
import com.BeilsangServer.domain.auth.dto.KakaoResponseDto;
import com.BeilsangServer.domain.auth.util.SecurityUtil;
import com.BeilsangServer.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class TestController {

    @GetMapping("/api/test1")
    @Operation(summary = "Some operation", security = @SecurityRequirement(name = "bearerAuth"))
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Long> test1(){
        Long id = SecurityUtil.getCurrentUserId();
        return ApiResponse.onSuccess(id);
    }
}

