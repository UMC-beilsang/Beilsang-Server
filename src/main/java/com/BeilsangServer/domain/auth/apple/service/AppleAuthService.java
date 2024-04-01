package com.BeilsangServer.domain.auth.apple.service;


import com.BeilsangServer.domain.auth.apple.AppleClient;
import com.BeilsangServer.domain.auth.apple.ApplePublicKeyGenerator;
import com.BeilsangServer.domain.auth.apple.AppleTokenParser;
import com.BeilsangServer.domain.auth.apple.dto.ApplePublicKeys;
import com.BeilsangServer.domain.auth.apple.dto.AppleMemberAndExistDto;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.global.enums.Provider;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import com.BeilsangServer.global.enums.Role;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleAuthService {

    private final AppleTokenParser appleTokenParser;
    private final AppleClient appleClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final MemberRepository memberRepository;

    @Transactional
    public AppleMemberAndExistDto getAppleMemberInfo(String idToken) {
        // idToken의 헤더를 파싱하고 appleTokenHeader에 저장
        final Map<String, String> appleTokenHeader = appleTokenParser.parseHeader(idToken);
        // Apple의 공개키를 가져와 applePublicKeys에 저장
        final ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
        // applePublicKeyGenerator를 이용해 공개키를 생성하고 이를 publicKey에 저장
        final PublicKey publicKey = applePublicKeyGenerator.generate(appleTokenHeader, applePublicKeys);
        // idToken과 publicKey를 이용해 클레임을 추출하고  claims에 저장
        final Claims claims = appleTokenParser.extractClaims(idToken, publicKey);

        // claims로부터 socialId, email 추출
        String socialId = claims.getSubject();
        String email = claims.get("email", String.class);

        Member member = Member.builder()
                .socialId(socialId)
                .email(email)
                .provider(Provider.valueOf("APPLE"))
                .role(Role.USER)
                .build();

        boolean existMember = false;

        if(memberRepository.findBySocialId(member.getSocialId()) != null){//DB에 회원정보 있으면 existMember = true
            existMember = true;
        }
        else{
            memberRepository.save(member); // DB에 회원정보 없으면 저장
        }

            return AppleMemberAndExistDto.builder()
                    .member(member)
                    .existMember(existMember)
                    .build();
        }
    }



