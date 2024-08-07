package com.BeilsangServer.global.common.apiPayload.code.status;

import com.BeilsangServer.global.common.apiPayload.code.BaseErrorCode;
import com.BeilsangServer.global.common.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),


    // 멤버 관련 응답
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "멤버가 없습니다."),
    JOIN_DUPLICATE_CHALLENGE(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER5001", "해당 챌린지에 이미 참여되어 있습니다."),

    // 챌린지 관련 응답
    CHALLENGE_NOT_FOUND(HttpStatus.NOT_FOUND,"CHALLENGE4001", "챌린지가 없습니다.") ,
    CHALLENGE_HOST_NOT_FOUND(HttpStatus.NOT_FOUND, "CHALLENGE4002", "챌린지의 호스트가 없습니다."),
    POINT_LACK(HttpStatus.INTERNAL_SERVER_ERROR, "CHALLENGE5001", "포인트가 부족합니다."),
    CHALLENGE_INSUFFICIENT(HttpStatus.INTERNAL_SERVER_ERROR, "CHALLENGE5002", "챌린지가 부족합니다."),


    // 피드 관련 응답
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND,"FEED4001","피드가 없습니다."),

    // 기타
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"CATEGORY4001", "카테고리가 없습니다."),

    // 인증 관련
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"AUTH401","인증되지 않은 요청입니다"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH401","유효하지않은 액세스 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,"AUTH401","유효하지않은 리프레시 토큰입니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"AUTH400","잘못된 요청입니다."),
    NOT_EXIST_USER(HttpStatus.UNAUTHORIZED,"AUTH401","존재하지 않는 유저입니다.")


    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
