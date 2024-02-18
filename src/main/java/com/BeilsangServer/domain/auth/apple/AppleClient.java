package com.BeilsangServer.domain.auth.apple;

import com.BeilsangServer.domain.auth.apple.dto.ApplePublicKeys;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


/*
애플 서버로부터 public key 받아옴
 */
@FeignClient(name = "apple-public-key", url = "https://appleid.apple.com")
public interface AppleClient {

    @GetMapping("/auth/keys")
    ApplePublicKeys getApplePublicKeys();
}


