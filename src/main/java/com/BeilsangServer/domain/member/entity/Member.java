package com.BeilsangServer.domain.member.entity;

import com.BeilsangServer.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;


    @Enumerated(EnumType.STRING)
    private Provider provider; // KAKAO, APPLE

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private LocalDate birth;

    private String address;

    private String keyword;

    private String discoveredPath;

    private String resolution;

    private int totalPoint;

    private String recommendNickname;

    private String profileUrl;

    @Builder
    public Member(
            Gender gender,
            Provider provider,
            String nickName,
            LocalDate birth,
            String address,
            String keyword,
            String discoveredPath,
            String resolution,
            int totalPoint,
            String recommendNickname,
            String profileUrl) {
        this.gender = gender;
        this.provider = provider;
        this.nickName = nickName;
        this.birth = birth;
        this.address = address;
        this.keyword = keyword;
        this.discoveredPath = discoveredPath;
        this.resolution = resolution;
        this.totalPoint = totalPoint;
        this.recommendNickname = recommendNickname;
        this.profileUrl = profileUrl;

    }


    public void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }


}





