package com.BeilsangServer.global.common.exception.handler;

import com.BeilsangServer.global.common.apiPayload.code.BaseErrorCode;
import com.BeilsangServer.global.common.exception.GeneralException;

public class ErrorHandler extends GeneralException {

    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);}
}
