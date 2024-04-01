package com.BeilsangServer.domain.member.entity;

import com.BeilsangServer.domain.member.dto.MemberLoginDto;
import com.BeilsangServer.domain.member.dto.MemberUpdateDto;
import com.BeilsangServer.global.common.BaseEntity;
import com.BeilsangServer.global.enums.Category;
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

    private String socialId;

    private String nickName;

    private LocalDate birth;

    private String address;

//    private String keyword;

    @Enumerated(EnumType.STRING)
    private Category keyword;

    private String discoveredPath;

    private String resolution;

    private int point;

    private String recommendNickname;

    private String profileUrl;

    private String refreshToken;

    private String deviceToken;


    public void setMemberInfo(MemberLoginDto memberLoginDto){
        if(memberLoginDto.getAddress() != null && !memberLoginDto.getAddress().isBlank()){
            this.address = memberLoginDto.getAddress();
        }
        if(memberLoginDto.getDiscoveredPath() != null && !memberLoginDto.getDiscoveredPath().isBlank()){
            this.discoveredPath = memberLoginDto.getDiscoveredPath();
        }
        if(memberLoginDto.getRecommendNickname() != null && !memberLoginDto.getRecommendNickname().isBlank()){
            this.recommendNickname = memberLoginDto.getRecommendNickname();
        }
        this.gender = memberLoginDto.getGender();
        this.nickName = memberLoginDto.getNickName();
        this.birth = LocalDate.parse(memberLoginDto.getBirth());
        this.keyword = Category.from(memberLoginDto.getKeyword());
        this.resolution = memberLoginDto.getResolution();
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

    public void updateProfileImageUrl(String profileUrl){
        this.profileUrl = profileUrl;
    }

    // 포인트 차감, 적립
    public void addPoint(Integer point) {
        this.point += point;
    }

    public void subPoint(Integer point) {
        this.point -= point;
    }
    public void updateDeviceToken(String deviceToken){ this.deviceToken = deviceToken; }
}





