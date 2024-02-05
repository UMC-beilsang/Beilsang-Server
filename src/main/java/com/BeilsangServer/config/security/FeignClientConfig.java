package com.BeilsangServer.config.security;

import com.BeilsangServer.BeilsangServerApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = BeilsangServerApplication.class)
public class FeignClientConfig {
}