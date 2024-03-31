package com.BeilsangServer.domain.auth.apple;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.security.*;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
public class AppleTokenParserTest {

    private final AppleTokenParser appleTokenParser = new AppleTokenParser(new ObjectMapper());


    @Test
    public void 애플_토큰_헤더_파싱_테스트() throws NoSuchAlgorithmException {
        // given
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        String appleToken = Jwts.builder()
                .setHeaderParam("kid", "kid 값")
                .claim("email", "test@test.com")
                .setIssuer("https://appleid.apple.com")
                .setIssuedAt(now)
                .setAudience("aud 값")
                .setSubject("sub_test")
                .setExpiration(new Date(now.getTime() + 60 * 60 * 1000))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();


        // when
        Map<String, String> headers = appleTokenParser.parseHeader(appleToken);

        // given
        assertThat(headers).containsKeys("alg", "kid");
    }

    @Test
    public void 애플_클레임_파싱_테스트() throws NoSuchAlgorithmException {
        // given
        Date now = new Date();
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA")
                .generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String appleToken = Jwts.builder()
                .setHeaderParam("kid", "kid 값")
                .claim("email", "test@test.com")
                .setIssuer("https://appleid.apple.com")
                .setIssuedAt(now)
                .setAudience("aud 값") // 수정된 부분
                .setSubject("sub_test")
                .setExpiration(new Date(now.getTime() + 60 * 60 * 1000))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();


        // when
        Claims claims = appleTokenParser.extractClaims(appleToken, publicKey);

        // then
        assertThat(claims.get("email")).isEqualTo("test@test.com");
    }
}