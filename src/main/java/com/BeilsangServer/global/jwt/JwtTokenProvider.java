package com.BeilsangServer.global.jwt;


import com.BeilsangServer.global.jwt.exception.CustomException;
import com.BeilsangServer.global.jwt.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Random;

@Slf4j
@Component
@Service
public class JwtTokenProvider implements InitializingBean {

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;  // 14일

    private final String secretKey; // Jwt 시크릿 키
    private static Key key;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }


    /*
    비밀키를 Base64 인코딩하고 다시 키로 변환하여 저장합
     */
    @Override
    public void afterPropertiesSet() {
        this.key = getKeyFromBase64EncodedKey(encodeBase64SecretKey(secretKey));
    }

    //여기서 payload는 멤버의 socialId
    public String createAccessToken(String payload){
        return createToken(payload, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(){
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        return createToken(generatedString, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String createToken(String payload, long expireLength){
        Claims claims = Jwts.claims().setSubject(payload);
        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLength);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /*
    페이로드 추출
     */
    public String getPayload(String token){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }catch (ExpiredJwtException e){
            return e.getClaims().getSubject();
        }catch (JwtException e){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    // 토큰의 유효성 검사
    public boolean validateToken(String token){
        try{
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException exception){
            return false;
        }
    }

    private String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);

        Key key = Keys.hmacShaKeyFor(keyBytes);

        return key;
    }

    //클라이언트 쿠키에 리프레시토큰 저장 시켜주는 메소드
    public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
        Long age = REFRESH_TOKEN_EXPIRE_TIME;
        Cookie cookie = new Cookie("refresh_token",refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(age.intValue());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
//    private final Key key;
//    private final TokenRepository tokenRepository;
//    private final UserDetailsService userDetailsService;
//
//    private static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String AUTHORITIES_KEY = "auth";
//    private static final String BEARER_TYPE = "Bearer";
//    private static final String TYPE_ACCESS = "access";
//    private static final String TYPE_REFRESH = "refresh";
//    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
//    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 14;  // 14일
//
//
//    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey, TokenRepository tokenRepository, UserDetailsService userDetailsService) {
//        this.tokenRepository = tokenRepository;
//        this.userDetailsService = userDetailsService;
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    // Kakao OAuth2 토큰 생성
//    public MemberResponseDto.TokenInfo generateKakaoToken(Member kakaoInfo) {
//        Claims claims = Jwts.claims().setSubject(kakaoInfo.getSocialId());
//
//        // 추가적인 사용자 정보를 토큰에 추가할 경우
//        claims.put("email", kakaoInfo.getEmail());
//
//        long now = System.currentTimeMillis();
//        long accessTokenExpirationTime = now + ACCESS_TOKEN_EXPIRE_TIME;
//        long refreshTokenExpirationTime = now + REFRESH_TOKEN_EXPIRE_TIME;
//
//        Token tokens = tokenRepository.findById(kakaoInfo.getId()).orElse(null);
//
//        String accessToken;
//        String refreshToken;
//
//        accessToken = Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(now))
//                .claim("type", TYPE_ACCESS)
//                .setExpiration(new Date(accessTokenExpirationTime))
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//        refreshToken = Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(now))
//                .claim("type", TYPE_REFRESH)
//                .setExpiration(new Date(refreshTokenExpirationTime))
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//        if (tokens != null) {
//            // 이미 가입된 사용자인 경우, 기존의 토큰을 업데이트합니다.
//
//            tokens.setAccessToken(accessToken);
//            tokens.setAccessTokenExpirationTime(accessTokenExpirationTime);
//            tokens.setRefreshToken(refreshToken);
//            tokens.setRefreshTokenExpirationTime(refreshTokenExpirationTime);
//
//        } else {
//            // 새로운 사용자인 경우, 새로운 토큰을 생성합니다.
//
//            tokens = Token.builder()
//                    .accessToken(accessToken)
//                    .accessTokenExpirationTime(accessTokenExpirationTime)
//                    .refreshToken(refreshToken)
//                    .refreshTokenExpirationTime(refreshTokenExpirationTime)
//                    .member(kakaoInfo)
//                    .build();
//
//        }
//
//        // 토큰 저장
//        tokenRepository.save(tokens);
//
//        return MemberResponseDto.TokenInfo.builder()
//                .grantType(BEARER_TYPE)
//                .accessToken(accessToken)
//                .accessTokenExpirationTime(accessTokenExpirationTime)
//                .refreshToken(refreshToken)
//                .refreshTokenExpirationTime(refreshTokenExpirationTime)
//                .build();
//    }
//
//    public MemberResponseDto.TokenInfo generateAppleToken(Member appleInfo) {
//        Claims claims = Jwts.claims().setSubject(appleInfo.getSocialId());
//
//        // 추가적인 사용자 정보를 토큰에 추가할 경우
//        claims.put("email", appleInfo.getEmail());
//
//        long now = System.currentTimeMillis();
//        long accessTokenExpirationTime = now + ACCESS_TOKEN_EXPIRE_TIME;
//        long refreshTokenExpirationTime = now + REFRESH_TOKEN_EXPIRE_TIME;
//
//        Token tokens = tokenRepository.findById(appleInfo.getId()).orElse(null);
//        String accessToken, refreshToken;
//
//        accessToken = Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(now))
//                .claim("type", TYPE_ACCESS)
//                .setExpiration(new Date(accessTokenExpirationTime))
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//        refreshToken = Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(new Date(now))
//                .claim("type", TYPE_REFRESH)
//                .setExpiration(new Date(refreshTokenExpirationTime))
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//        if (tokens != null) {
//            // 이미 가입된 사용자인 경우, 기존의 토큰을 업데이트합니다.
//
//            tokens.setAccessToken(accessToken);
//            tokens.setAccessTokenExpirationTime(accessTokenExpirationTime);
//            tokens.setRefreshToken(refreshToken);
//            tokens.setRefreshTokenExpirationTime(refreshTokenExpirationTime);
//
//        } else {
//            // 새로운 사용자인 경우, 새로운 토큰을 생성합니다.
//
//            tokens = Token.builder()
//                    .accessToken(accessToken)
//                    .accessTokenExpirationTime(accessTokenExpirationTime)
//                    .refreshToken(refreshToken)
//                    .refreshTokenExpirationTime(refreshTokenExpirationTime)
//                    .member(appleInfo)
//                    .build();
//
//        }
//
//        // 토큰 저장
//        tokenRepository.save(tokens);
//
//        return MemberResponseDto.TokenInfo.builder()
//                .grantType(BEARER_TYPE)
//                .accessToken(accessToken)
//                .accessTokenExpirationTime(accessTokenExpirationTime)
//                .refreshToken(refreshToken)
//                .refreshTokenExpirationTime(refreshTokenExpirationTime)
//                .build();
//    }
//
//    public String extractEmail(String token) {
//        return Jwts.parser()
//                .setSigningKey(key)
//                .parseClaimsJws(token)
//                .getBody()
//                .get("email", String.class); // 토큰 payload에서 email 값을 추출
//    }
//
//    // 토큰의 유효성을 검사하고 Authentication 객체를 리턴하는 메서드
//    public Authentication getAuthentication(String token) {
//        String email = extractEmail(token); // 토큰에서 email 추출
//        UserDetails userDetails = userDetailsService.loadUserByUsername(email); // email을 사용하여 UserDetails 가져옴
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }
//
//
//
//    //토큰 정보를 검증하는 메서드
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (SecurityException | MalformedJwtException e) {
//            log.info("Invalid JWT Token", e);
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT Token", e);
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT Token", e);
//        } catch (IllegalArgumentException e) {
//            log.info("JWT claims string is empty.", e);
//        }
//        return false;
//    }
//
//    private Claims parseClaims(String accessToken) {
//        try {
//            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
//        } catch (ExpiredJwtException e) {
//            // ???
//            return e.getClaims();
//        }
//    }
//
//    public String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}


