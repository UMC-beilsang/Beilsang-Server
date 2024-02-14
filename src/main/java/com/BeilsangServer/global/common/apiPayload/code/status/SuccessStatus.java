package com.BeilsangServer.global.common.apiPayload.code.status;

import com.BeilsangServer.global.common.apiPayload.code.BaseCode;
import com.BeilsangServer.global.common.apiPayload.code.ReasonDTO;

public enum SuccessStatus implements BaseCode {
    ;

    @Override
    public ReasonDTO getReason() {
        return null;
    }

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return null;
    }
}
