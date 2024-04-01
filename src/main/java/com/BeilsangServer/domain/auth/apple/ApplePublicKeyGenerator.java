package com.BeilsangServer.domain.auth.apple;


import com.BeilsangServer.domain.auth.apple.dto.ApplePublicKey;
import com.BeilsangServer.domain.auth.apple.dto.ApplePublicKeys;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

/*
n, e 를 사용해 RSA 공개키를 생성한다. 생성에 성공했다면, id_token 의 Claim 추출에 사용
 */
@Component
public class ApplePublicKeyGenerator {

    private static final String SIGN_ALGORITHM_HEADER = "alg"; // 암호화 알고리즘
    private static final String KEY_ID_HEADER = "kid"; // 키 ID를 나타내는 헤더의 이름
    private static final int POSITIVE_SIGN_NUMBER = 1;

    // 공개키 생성 메서드
    public PublicKey generate( Map<String, String> headers, final ApplePublicKeys publicKeys) {
        // 헤더에서 암호화 알고리즘과 키 ID를 가져와 Apple의 공개 키 중에서 일치하는 키를 찾아 applePublicKey에 저장
         ApplePublicKey applePublicKey = publicKeys.getMatchingKey(
                headers.get(SIGN_ALGORITHM_HEADER),
                headers.get(KEY_ID_HEADER)
        );
        // applePublicKey를 이용해 공개 키를 생성하고 반환
        return generatePublicKey(applePublicKey);
    }

    // Apple의 공개 키를 이용해 공개 키를 생성하는 메서드
    private PublicKey generatePublicKey(final ApplePublicKey applePublicKey) {
        // Apple의 공개 키에서 n과 e 값을 가져와 Base64 디코딩
        final byte[] nBytes = Base64.getUrlDecoder().decode(applePublicKey.getN());
        final byte[] eBytes = Base64.getUrlDecoder().decode(applePublicKey.getE());

        // 디코딩한 n과 e 값을 이용해 BigInteger를 생성
        final BigInteger n = new BigInteger(POSITIVE_SIGN_NUMBER, nBytes);
        final BigInteger e = new BigInteger(POSITIVE_SIGN_NUMBER, eBytes);
        // BigInteger로 생성한 n과 e 값을 이용해 RSAPublicKeySpec를 생성
         RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);

        try {
            // KeyFactory 인스턴스를 생성하고 공개 키를 생성하여 반환
            KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.getKty());
            return keyFactory.generatePublic(rsaPublicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new IllegalStateException("Apple OAuth 로그인 중 public key 생성에 문제가 발생했습니다.");
        }
    }
}
