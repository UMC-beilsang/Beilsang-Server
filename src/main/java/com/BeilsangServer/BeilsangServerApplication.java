package com.BeilsangServer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableJpaAuditing
@OpenAPIDefinition(servers = {@Server(url = "https://beilsang.com", description = "비일상 서버")})
public class BeilsangServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(BeilsangServerApplication.class, args);
	}

}
