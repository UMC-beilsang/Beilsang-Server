package com.BeilsangServer.domain.auth.service;


import com.BeilsangServer.domain.auth.dto.AppleMemberDto;
import com.BeilsangServer.domain.auth.dto.KakaoMemberDto;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.entity.Provider;
import com.BeilsangServer.domain.member.entity.Role;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;



@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private static final OkHttpClient client = new OkHttpClient();
    private static final String KAKAO_ID = "4bcf6f023bec74eb18985f6e61a5637d";


    public Member getAppleInfo(AppleMemberDto appleMemberDto) throws IOException, ParseException {

        String email = appleMemberDto.getEmail();
        if(email==null){
            throw new IllegalArgumentException("Null Error");
        }




        // 이미 존재하는 이메일인지 확인
        Optional<Member> existingMember = memberRepository.findByEmail(email);
        System.out.println(existingMember);
        // 만약 이미 존재하는 사용자인 경우, 해당 사용자 객체를 반환
        if (existingMember.isPresent()) {
            return existingMember.get();
        }

        // Member 생성
        Member appleMember = Member.builder()
                .socialId(appleMemberDto.getSocialId())
                .email(appleMemberDto.getEmail())
                .provider(Provider.APPLE)
                .role(Role.ROLE_USER)
                .build();


        memberRepository.save(appleMember);
        return appleMember;
    }

    public Member getKakaoInfo(KakaoMemberDto kakaoMemberDto) throws IOException, ParseException {

        String email = kakaoMemberDto.getEmail();
        if(email==null){
            throw new IllegalArgumentException("Null error");
        }

        // 이미 존재하는 이메일인지 확인
        Optional<Member> existingMember = memberRepository.findByEmail(email);

        System.out.println(existingMember);
        // 만약 이미 존재하는 사용자인 경우, 해당 사용자 객체를 반환
        if (existingMember.isPresent()) {
            return existingMember.get();
        }


        // Member 생성
        Member kakaoMember = Member.builder()
                .socialId(kakaoMemberDto.getSocialId())
                .email(kakaoMemberDto.getEmail())
                .provider(Provider.APPLE)
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(kakaoMember);
        return kakaoMember;
    }





    public Member getKakaoInfoUsingToken(String accessToken) throws IOException, ParseException {

        // 예시로 주어진 액세스 토큰
        String accessTokenJson = accessToken;

        // "{"와 "}"를 제거하여 액세스 토큰 값만 추출
        String token = accessTokenJson.substring(accessTokenJson.indexOf(":\"") + 2, accessTokenJson.lastIndexOf("\""));

        // 추출한 액세스 토큰 출력
        System.out.println(token);

        String url = "https://kapi.kakao.com/v2/user/me";
        Request.Builder builder = new Request.Builder()
                .header("Authorization","Bearer "+token)
                .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                .url(url);
        Request request = builder.build();
        System.out.println(request);

        Response responseHTML = client.newCall(request).execute();

        net.minidev.json.parser.JSONParser parser = new JSONParser();
        net.minidev.json.JSONObject info =(net.minidev.json.JSONObject) parser.parse(responseHTML.body().string());

        System.out.println(accessToken);


        // email 값 추출
        net.minidev.json.JSONObject kakaoAccount = (net.minidev.json.JSONObject) info.get("kakao_account");
        String email = (String) kakaoAccount.get("email");

        // 이미 존재하는 이메일인지 확인
        Optional<Member> existingMember = memberRepository.findByEmail(email);
        System.out.println(existingMember);
        // 만약 이미 존재하는 사용자인 경우, 해당 사용자 객체를 반환
        if (existingMember.isPresent()) {
            return existingMember.get();
        }

        // Member 생성
        Member member = Member.builder()
                .email(email)
                .provider(Provider.KAKAO)
                .role(Role.ROLE_USER)
                .build();

        memberRepository.save(member);
        return member;
    }


}