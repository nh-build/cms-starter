package com.cmsstarter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 관리자 영역: ROLE_ADMIN 만 접근. 별도 로그인 페이지 사용. */
    @Bean
    @Order(1)
    SecurityFilterChain adminChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**")
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/admin/login").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .formLogin(f -> f
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/admin/login?error"))
                .logout(l -> l.logoutUrl("/admin/logout").logoutSuccessUrl("/admin/login?logout"));
        return http.build();
    }

    /** 스토어프론트: 둘러보기는 공개, 장바구니/주문은 로그인 필요. */
    @Bean
    @Order(2)
    SecurityFilterChain shopChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a
                        .requestMatchers("/cart/**", "/checkout/**", "/orders/**", "/mypage/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(f -> f
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", false)
                        .failureUrl("/login?error"))
                .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/"));
        return http.build();
    }
}
