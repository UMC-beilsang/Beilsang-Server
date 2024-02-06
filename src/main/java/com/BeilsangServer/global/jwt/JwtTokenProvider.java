package com.BeilsangServer.global.jwt;

import com.BeilsangServer.domain.auth.entity.token.Token;
import com.BeilsangServer.domain.auth.entity.token.TokenRepository;
import com.BeilsangServer.domain.member.entity.Member;
import com.BeilsangServer.global.jwt.dto.MemberResponseDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60;            // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;  // 14일


    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey, TokenRepository tokenRepository, UserDetailsService userDetailsService) {
        this.tokenRepository = tokenRepository;
        this.userDetailsService = userDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Kakao OAuth2 토큰 생성
    public MemberResponseDto.TokenInfo generateKakaoToken(Member kakaoInfo) {
        Claims claims = Jwts.claims().setSubject(kakaoInfo.getSocialId());

        // 추가적인 사용자 정보를 토큰에 추가할 경우
        claims.put("email", kakaoInfo.getEmail());

        long now = System.currentTimeMillis();
        long accessTokenExpirationTime = now + ACCESS_TOKEN_EXPIRE_TIME;
        long refreshTokenExpirationTime = now + REFRESH_TOKEN_EXPIRE_TIME;

        Token tokens = tokenRepository.findById(kakaoInfo.getId()).orElse(null);

        String accessToken;
        String refreshToken;

        accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .claim("type", TYPE_ACCESS)
                .setExpiration(new Date(accessTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .claim("type", TYPE_REFRESH)
                .setExpiration(new Date(refreshTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        if (tokens != null) {
            // 이미 가입된 사용자인 경우, 기존의 토큰을 업데이트합니다.

            tokens.setAccessToken(accessToken);
            tokens.setAccessTokenExpirationTime(accessTokenExpirationTime);
            tokens.setRefreshToken(refreshToken);
            tokens.setRefreshTokenExpirationTime(refreshTokenExpirationTime);

        } else {
            // 새로운 사용자인 경우, 새로운 토큰을 생성합니다.

            tokens = Token.builder()
                    .accessToken(accessToken)
                    .accessTokenExpirationTime(accessTokenExpirationTime)
                    .refreshToken(refreshToken)
                    .refreshTokenExpirationTime(refreshTokenExpirationTime)
                    .member(kakaoInfo)
                    .build();

        }

        // 토큰 저장
        tokenRepository.save(tokens);

        return MemberResponseDto.TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpirationTime(accessTokenExpirationTime)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(refreshTokenExpirationTime)
                .build();
    }

    public MemberResponseDto.TokenInfo generateAppleToken(Member appleInfo) {
        Claims claims = Jwts.claims().setSubject(appleInfo.getSocialId());

        // 추가적인 사용자 정보를 토큰에 추가할 경우
        claims.put("email", appleInfo.getEmail());

        long now = System.currentTimeMillis();
        long accessTokenExpirationTime = now + ACCESS_TOKEN_EXPIRE_TIME;
        long refreshTokenExpirationTime = now + REFRESH_TOKEN_EXPIRE_TIME;

        Token tokens = tokenRepository.findById(appleInfo.getId()).orElse(null);
        String accessToken, refreshToken;

        accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .claim("type", TYPE_ACCESS)
                .setExpiration(new Date(accessTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .claim("type", TYPE_REFRESH)
                .setExpiration(new Date(refreshTokenExpirationTime))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        if (tokens != null) {
            // 이미 가입된 사용자인 경우, 기존의 토큰을 업데이트합니다.

            tokens.setAccessToken(accessToken);
            tokens.setAccessTokenExpirationTime(accessTokenExpirationTime);
            tokens.setRefreshToken(refreshToken);
            tokens.setRefreshTokenExpirationTime(refreshTokenExpirationTime);

        } else {
            // 새로운 사용자인 경우, 새로운 토큰을 생성합니다.

            tokens = Token.builder()
                    .accessToken(accessToken)
                    .accessTokenExpirationTime(accessTokenExpirationTime)
                    .refreshToken(refreshToken)
                    .refreshTokenExpirationTime(refreshTokenExpirationTime)
                    .member(appleInfo)
                    .build();

        }

        // 토큰 저장
        tokenRepository.save(tokens);

        return MemberResponseDto.TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpirationTime(accessTokenExpirationTime)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(refreshTokenExpirationTime)
                .build();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class); // 토큰 payload에서 email 값을 추출
    }

    // 토큰의 유효성을 검사하고 Authentication 객체를 리턴하는 메서드
    public Authentication getAuthentication(String token) {
        String email = extractEmail(token); // 토큰에서 email 추출
        UserDetails userDetails = userDetailsService.loadUserByUsername(email); // email을 사용하여 UserDetails 가져옴
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }



    //토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // ???
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}


