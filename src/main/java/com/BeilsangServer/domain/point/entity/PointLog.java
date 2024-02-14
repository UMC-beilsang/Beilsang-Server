package com.BeilsangServer.domain.point.entity;

import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.global.common.BaseEntity;
import com.BeilsangServer.global.enums.PointName;
import com.BeilsangServer.global.enums.PointStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointLog extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private PointName pointName;

    @Enumerated(EnumType.STRING)
    private PointStatus status;

    private int value;

//    private LocalDate date;

    private int period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
