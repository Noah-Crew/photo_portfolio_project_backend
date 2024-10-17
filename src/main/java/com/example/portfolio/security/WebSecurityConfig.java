package com.example.portfolio.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
	    // csrf 비활성화
	    .csrf(csrf -> csrf.disable())
	    // H2 콘솔을 위한 헤더 설정
	    .headers(headers -> headers
	        .contentSecurityPolicy(csp -> csp
	            .policyDirectives("frame-ancestors 'self'")
	        )
	    )
	    .authorizeHttpRequests(auth -> auth
	        // H2 콘솔에 대한 접근 허용
	        .requestMatchers("/h2-console/**").permitAll()
	        // 특정 요청 보안 설정
	        .requestMatchers("/Admin/**").authenticated()
	        // permitAll() : 인증 없이 접근 허용 
	        .anyRequest().permitAll()	
	    )
	    .formLogin(form -> form
	        // 로그인 성공 시 이동할 url
	        .defaultSuccessUrl("/Admin/ManageImages")
	        .permitAll()
	    );

	return http.build();
	}
	
	// 암호화 알고리즘
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}