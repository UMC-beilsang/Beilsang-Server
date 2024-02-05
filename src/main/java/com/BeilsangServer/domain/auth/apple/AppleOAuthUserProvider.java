//package com.BeilsangServer.domain.auth.apple;
//
//import com.BeilsangServer.domain.auth.apple.dto.OAuthPlatformMemberResponse;
//import com.BeilsangServer.domain.auth.apple.exception.InvalidAccessTokenException;
//import io.jsonwebtoken.Claims;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.security.PublicKey;
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class AppleOAuthUserProvider {
//
//    private final AppleJwtParser appleJwtParser;
//    private final AppleClient appleClient;
//    private final PublicKeyGenerator publicKeyGenerator;
//    private final AppleClaimsValidator appleClaimsValidator;
//
//    public OAuthPlatformMemberResponse getApplePlatformMember(String identityToken) {
//        Map<String, String> headers = appleJwtParser.parseHeaders(identityToken);
//        ApplePublicKeys applePublicKeys = appleClient.getApplePublicKeys();
//
//        PublicKey publicKey = publicKeyGenerator.generatePublicKey(headers, applePublicKeys);
//
//        Claims claims = appleJwtParser.parsePublicKeyAndGetClaims(identityToken, publicKey);
//        validateClaims(claims);
//        return new OAuthPlatformMemberResponse(claims.getSubject(), claims.get("email", String.class));
//    }
//
//    private void validateClaims(Claims claims) {
//        if (!appleClaimsValidator.isValid(claims)) {
//            throw new InvalidAccessTokenException("Apple OAuth Claims 값이 올바르지 않습니다.");
//        }
//    }
//}
