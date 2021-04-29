package com.pmc.market.config;

import com.pmc.market.error.LoginFailHandler;
import com.pmc.market.oauth.KakaoOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

//@Order(1)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().headers().frameOptions().disable()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/my", "/oauth2/**", "/login/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/my")
                .userInfoEndpoint()
                .customUserType(KakaoOAuth2User.class, "kakao")
        ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/**", "/configuration/ui", "/swagger-resources/**",
                "/configuration/security", "/swagger-ui.html/**", "/webjars/**", "/swagger**", "/users/sign-up");
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new LoginFailHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}