package com.BeilsangServer.domain.auth.service;

import com.BeilsangServer.domain.auth.dto.KakaoInfoDto;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class KakaoAuthService {
    private final MemberRepository memberRepository;

    // 카카오Api 호출해서 AccessToken으로 유저정보 가져오기(id, email)
    public Map<String, Object> getUserAttributesByToken(String accessToken){
        return WebClient.create()
                .get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }

    // 카카오API에서 가져온 유저정보를 DB에 저장
    public Member getUserProfileByToken(String accessToken){
        Map<String, Object> userAttributesByToken = getUserAttributesByToken(accessToken);
        KakaoInfoDto kakaoInfoDto = new KakaoInfoDto(userAttributesByToken);
        Member member = Member.builder()
                .socialId(kakaoInfoDto.getSocialId())
                .email(kakaoInfoDto.getEmail())
                .build();

        if(memberRepository.findBySocialId(member.getSocialId()) != null) //DB에 회원정보 있으면 그냥 넘김
        {
        } else {
            memberRepository.save(member); //DB에 회원정보 없으면 저장
        }
        return member;

    }
}