package com.BeilsangServer.domain.auth.apple.service;

import com.BeilsangServer.domain.auth.apple.AppleClaimsValidator;
import com.BeilsangServer.domain.auth.apple.AppleClient;
import com.BeilsangServer.domain.auth.apple.AppleJwtParser;
import com.BeilsangServer.domain.auth.apple.PublicKeyGenerator;
import com.BeilsangServer.domain.auth.apple.dto.ApplePublicKeys;
import com.BeilsangServer.domain.auth.apple.dto.AppleMemberAndExistDto;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.domain.member.entity.Provider;
import com.BeilsangServer.domain.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleAuthService {

    private final AppleJwtParser appleJwtParser;
    private final AppleClient appleClient;
    private final PublicKeyGenerator publicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;
    private final MemberRepository memberRepository;

    @Transactional
    public AppleMemberAndExistDto getAppleMemberInfo(String identityToken) {
        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();

        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);

        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
        validateClaims(claims);

        Long socialId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);

        Member member = Member.builder()
                .socialId(socialId)
                .email(email)
                .provider(Provider.valueOf("APPLE"))
                .build();

        boolean existMember = false;

        if(memberRepository.findBySocialId(member.getSocialId()) != null) //DB에 회원정보 있으면 그냥 넘김

        {existMember = true;

        } else {
            memberRepository.save(member); //DB에 회원정보 없으면 저장
        }

        return AppleMemberAndExistDto.builder()
                .member(member)
                .existMember(existMember)
                .build();
    }

    private void validateClaims(Claims claims) {
        if (!appleClaimsValidator.isValid(claims)) {
            throw new RuntimeException("Apple OAuth Claims 값이 올바르지 않습니다.");
        }
    }
}