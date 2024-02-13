package com.BeilsangServer.domain.point.dto;

import com.BeilsangServer.global.enums.PointStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class PointResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class pointLogDTO{
        private Long id;
        private String name;
        private PointStatus status;
        private Integer value;
        private LocalDate date;
        private Integer period;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class pointLogListDTO{
        private Integer total;
        private List<pointLogDTO> points;
    }

}
