package com.BeilsangServer.domain.challenge.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChallengePeriod {
    WEEK(7), MONTH(30);

    private final Integer days;
}
