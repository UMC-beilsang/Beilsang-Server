package com.BeilsangServer.domain.member.entity;

import com.BeilsangServer.domain.member.dto.MemberLoginDto;
import com.BeilsangServer.domain.member.dto.MemberUpdateDto;
import com.BeilsangServer.global.common.BaseEntity;
import com.BeilsangServer.global.enums.Gender;
import com.BeilsangServer.global.enums.Provider;
import com.BeilsangServer.global.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    private String email;

    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;


    @Enumerated(EnumType.STRING)
    private Provider provider; // KAKAO, APPLE

    private Long socialId;

    private String nickName;

    private LocalDate birth;

    private String address;

    private String keyword;

    private String discoveredPath;

    private String resolution;

    private int point;

    private String recommendNickname;

    private String profileUrl;

    private String refreshToken;

    public void setMemberInfo(MemberLoginDto memberLoginDto){
        this.gender = memberLoginDto.getGender();
        this.nickName = memberLoginDto.getNickName();
        this.birth = LocalDate.parse(memberLoginDto.getBirth(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.address = memberLoginDto.getAddress();
        this.keyword = memberLoginDto.getKeyword();
        this.discoveredPath = memberLoginDto.getDiscoveredPath();
        this.resolution = memberLoginDto.getResolution();
        this.recommendNickname = memberLoginDto.getRecommendNickname();

    }

    @Builder
    public Member(
            String email,
            Role role,
            Gender gender,
            Provider provider,
            Long socialId,
            String nickName,
            String birth,
            String address,
            String keyword,
            String discoveredPath,
            String resolution,
            int totalPoint,
            String recommendNickname,
            String profileUrl) {
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.provider = provider;
        this.socialId = socialId;
        this.nickName = nickName;
        this.birth = LocalDate.parse(birth,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.address = address;
        this.keyword = keyword;
        this.discoveredPath = discoveredPath;
        this.resolution = resolution;
        this.point = totalPoint;
        this.recommendNickname = recommendNickname;
        this.profileUrl = profileUrl;

    }


    public void updateProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    public void update(MemberUpdateDto memberUpdateDto){
        if(!memberUpdateDto.getNickName().isBlank()){
            this.nickName = memberUpdateDto.getNickName();
        }
        if(!memberUpdateDto.getBirth().isBlank()){
            this.birth = LocalDate.parse(memberUpdateDto.getBirth(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if(!memberUpdateDto.getGender().isBlank()){
            this.gender = Gender.valueOf(memberUpdateDto.getGender());
        }
        if(!memberUpdateDto.getAddress().isBlank()){
            this.address = memberUpdateDto.getAddress();
        }
    }

    // 포인트 차감, 적립
    public void addPoint(Integer point) {
        this.point += point;
    }

    public void subPoint(Integer point) {
        this.point -= point;
    }
}





