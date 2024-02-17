package com.BeilsangServer.global.enums;

import com.BeilsangServer.global.common.apiPayload.code.status.ErrorStatus;
import com.BeilsangServer.global.common.exception.handler.ErrorHandler;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@AllArgsConstructor
@Getter
public enum Category {

    ALL("전체"),
    TUMBLER("다회용컵"),
    REFILL_STATION("리필스테이션"),
    MULTIPLE_CONTAINERS("다회용기"),
    ECO_PRODUCT("친환경제품"),
    PLOGGING("플로깅"),
    VEGAN("비건"),
    PUBLIC_TRANSPORT("대중교통"),
    BIKE("자전거"),
    RECYCLE("재활용");

//    private static final Map<String, String> DES_MAP = Collections.unmodifiableMap(
//            Stream.of(values()).collect(Collectors.toMap(Category::getDescriptions, Category::name))
//    );
//    @Getter
//    private final String descriptions;
//    Category(String descriptions){
//        this.descriptions = descriptions;
//    }
//
//    public static Category of(final String descriptions){
//        return Category.valueOf(DES_MAP.get(descriptions));
//    }
    private String korName;

    Category(String korName) {
        this.korName = korName;
    }

    public String getKorName() {
        return korName;
    }

    @JsonCreator
    public static Category from(String stringCategory) {
        // 주어진 문자열 값과 매핑된 열거형 상수 반환
        for (Category category : Category.values()) {
            if (category.name().equalsIgnoreCase(stringCategory) || category.getKorName().equals(stringCategory)) {
                return category;
            }
        }
        throw new ErrorHandler(ErrorStatus.CATEGORY_NOT_FOUND);
    }

//    @JsonCreator
//    public static Category from(String stringCategory) {
//        return Category.valueOf(stringCategory.toUpperCase());
//    }
}
