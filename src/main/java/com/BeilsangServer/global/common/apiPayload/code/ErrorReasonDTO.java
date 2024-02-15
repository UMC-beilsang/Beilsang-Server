package com.BeilsangServer.global.common.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
@AllArgsConstructor
public class ErrorReasonDTO {

    private String message;
    private String code;
    private boolean isSuccess;
    private HttpStatus httpStatus;
}
