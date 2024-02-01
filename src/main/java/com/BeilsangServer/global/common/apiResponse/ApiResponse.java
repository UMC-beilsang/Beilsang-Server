package com.BeilsangServer.global.common.apiResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder({"isSuccess", "status", "message", "data"}) // 변수 순서를 지정
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean isSuccess;
    private String status;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public ApiResponse(ApiResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.status = status.getStatus();
        this.message = status.getMessage();
    }

    public ApiResponse(ApiResponseStatus status, T data) {
        this.isSuccess = status.isSuccess();
        this.status = status.getStatus();
        this.message = status.getMessage();
        this.data = data;
    }
    @JsonProperty("isSuccess")
    public boolean isSuccess() {
        return isSuccess;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("data")
    public T getData() {
        return data;
    }
}