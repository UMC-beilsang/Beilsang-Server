package com.BeilsangServer.domain.point.converter;

import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.point.dto.PointResponseDTO;
import com.BeilsangServer.domain.point.entity.PointLog;

import java.util.List;

public class PointConverter {

    public static PointResponseDTO.pointLogListDTO pointLogListDTO(List<PointLog> pointLogs, Member member){

        List<PointResponseDTO.pointLogDTO> points = pointLogs.stream().map(pointLog -> PointResponseDTO.pointLogDTO.builder()
                .id(pointLog.getId())
                .value(pointLog.getValue())
                .name(pointLog.getName())
                .period(pointLog.getPeriod())
                .date(pointLog.getCreatedAt().toLocalDate())
                .status(pointLog.getStatus())
                .build()
        ).toList();

        return PointResponseDTO.pointLogListDTO.builder().points(points).total(member.getPoint()).build();
    }
}
