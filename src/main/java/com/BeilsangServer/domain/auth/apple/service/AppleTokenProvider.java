package com.BeilsangServer.domain.auth.apple.service;

import com.BeilsangServer.domain.auth.apple.dto.AppleAuthTokenResponse;
import com.BeilsangServer.global.jwt.exception.CustomException;
import com.BeilsangServer.global.jwt.exception.ErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Service
public class AppleTokenProvider {

    private final String clientId;
    private final String keyId;
    private final String teamId;

    public AppleTokenProvider(
        @Value("${apple.client-id}") String clientId,
        @Value("${apple.key-id}") String keyId,
        @Value("${apple.team-id}")String teamId) {
        this.clientId = clientId;
        this.keyId = keyId;
        this.teamId = teamId;
    }

    public String createClientSecret() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", keyId); //apple key id
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(teamId) // apple team id
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
                .setExpiration(expirationDate) // 만료 시간
                .setAudience("https://appleid.apple.com")
                .setSubject(clientId) // bundle id
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    public PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        String appleSignKeyFilePath = "static/AuthKey_SPW385Z3Y9.p8";

        InputStream privateKey = new ClassPathResource(appleSignKeyFilePath).getInputStream();

        String result = new BufferedReader(new InputStreamReader(privateKey)) .lines().collect(Collectors.joining("\n"));

        String key = result.replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decodeBase64(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);


    }

    public AppleAuthTokenResponse GenerateAuthToken(String authorizationCode, String clientSecret) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String authUrl = "https://appleid.apple.com/auth/token";

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<AppleAuthTokenResponse> response = restTemplate.postForEntity(authUrl, httpEntity, AppleAuthTokenResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error(String.valueOf(e));
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

    }

}
