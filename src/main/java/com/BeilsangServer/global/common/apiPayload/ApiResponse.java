package com.BeilsangServer.global.common.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder({"isSuccess", "code", "message", "data"}) // 변수 순서를 지정
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean isSuccess;
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ApiResponse(ApiResponseStatus code) {
        this.isSuccess = code.isSuccess();
        this.code = code.getStatus();
        this.message = code.getMessage();
    }

    public ApiResponse(ApiResponseStatus code, T data) {
        this.isSuccess = code.isSuccess();
        this.code = code.getStatus();
        this.message = code.getMessage();
        this.data = data;
    }
    @JsonProperty("isSuccess")
    public boolean isSuccess() {
        return isSuccess;
    }

    @JsonProperty("status")
    public String getCode() {
        return code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("data")
    public T getData() {
        return data;
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }
}