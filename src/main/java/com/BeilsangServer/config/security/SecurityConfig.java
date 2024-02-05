package com.BeilsangServer.config.security;

import com.BeilsangServer.domain.member.service.MemberService;
import com.BeilsangServer.global.jwt.JwtFilter;
import com.BeilsangServer.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;


    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable()); // 로그인 폼 미사용

        http
                .httpBasic((auth) -> auth.disable()); // http basic 미사용

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/auth/**").authenticated()
                        .anyRequest().permitAll()); // 일단 임시로 허용

        //JWTFilter 등록

        http
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);


        //세션 방식 미사용
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
