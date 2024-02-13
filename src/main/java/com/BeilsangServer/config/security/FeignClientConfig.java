package com.BeilsangServer.config.security;

import com.BeilsangServer.BeilsangServerApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/*
 FeignClient을 스프링 빈으로 등록
 */
@Configuration
@EnableFeignClients(basePackageClasses = BeilsangServerApplication.class)
public class FeignClientConfig {
}