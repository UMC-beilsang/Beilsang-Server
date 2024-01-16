package com.BeilsangServer.domain.member.entity;

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
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    private String nickName;

    private LocalDate birth;

    private String address;

    private String keyword;

    private String discoveredPath;

    private String resolution;

    private int totalPoint;

}
