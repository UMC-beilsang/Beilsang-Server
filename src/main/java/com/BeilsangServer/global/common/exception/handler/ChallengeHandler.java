package com.BeilsangServer.global.common.exception.handler;

import com.BeilsangServer.global.common.apiPayload.code.BaseErrorCode;
import com.BeilsangServer.global.common.exception.GeneralException;

public class ChallengeHandler extends GeneralException {

    public ChallengeHandler(BaseErrorCode errorCode) {
        super(errorCode);}
}
