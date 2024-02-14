package com.BeilsangServer.global.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChallengePeriod {
    WEEK(7), MON(30);

    private final Integer days;
}


