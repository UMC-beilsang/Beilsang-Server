package com.BeilsangServer.global.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Category {

    TUMBLER("다회용컵"),
    REFILL_STATION("리필스테이션"),
    MULTIPLE_CONTAINERS("다회용기"),
    ECO_PRODUCT("친환경제품"),
    PLOGGING("플로깅"),
    VEGAN("비건"),
    PUBLIC_TRANSPORT("대중교통"),
    BIKE("자전거"),
    RECYCLE("재활용");

    private final String descriptions;

    @JsonCreator
    public static Category from(String stringCategory) {
        return Category.valueOf(stringCategory.toUpperCase());
    }
}
