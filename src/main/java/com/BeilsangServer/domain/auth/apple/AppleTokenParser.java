package com.BeilsangServer.domain.auth.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleTokenParser {

    //identity 토큰을 파싱하는 데 사용되는 상수, identity 토큰은 '.'으로 구분된 세 부분으로 이루어져 있음
    private static final String IDENTITY_TOKEN_VALUE_DELIMITER = "\\.";

    //Apple 토큰의 헤더 부분의 인덱스를 선언

    private static final int HEADER_INDEX = 0;

    private final ObjectMapper objectMapper ;

    public Map<String, String> parseHeader(final String idToken) {
        try {
            // Apple 토큰을 '.'으로 분리하고 첫 번째 부분인 헤더를 가져옴
            final String encodedHeader = idToken.split(IDENTITY_TOKEN_VALUE_DELIMITER)[HEADER_INDEX];
            // Base64로 인코딩된 헤더를 디코딩
            final String decodedHeader = new String(Base64.getUrlDecoder().decode(encodedHeader));
            //디코딩된 헤더를 Map 형태로 반환
            return objectMapper.readValue(decodedHeader, Map.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException("appleToken 값이 jwt 형식인지, 값이 정상적인지 확인해주세요.");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("디코드된 헤더를 Map 형태로 분류할 수 없습니다. 헤더를 확인해주세요.");
        }
    }

    //identity 토큰의 페이로드를 파싱하고 검증하는 메소드
    public Claims extractClaims(String idToken, PublicKey publicKey) {
        try {
            // identity 토큰을 파싱하고 검증한 후 페이로드를 반환
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 jwt 타입");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("비어있는 jwt");
        } catch (JwtException e) {
            throw new JwtException("jwt 검증 or 분석 오류");
        }
    }
}