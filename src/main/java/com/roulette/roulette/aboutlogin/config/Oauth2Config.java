package com.roulette.roulette.aboutlogin.config;

import com.roulette.roulette.aboutlogin.handler.AuthenHandler;
import com.roulette.roulette.aboutlogin.handler.EntryPointHandler;
import com.roulette.roulette.aboutlogin.handler.FailHandler;
import com.roulette.roulette.aboutlogin.handler.SuccessHandler;
import com.roulette.roulette.aboutlogin.jwt.JwtFilter;
import com.roulette.roulette.aboutlogin.service.CustomOauth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Oauth2Config {
    private final CustomOauth2Service customOatuh2Service;
    private final SuccessHandler sucessHandler;
    private final FailHandler failHandler;
    private final JwtFilter jwtFilter;
    private final AuthenHandler authenHandler;
    private final EntryPointHandler entryPointHandler;



    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakakoredirecturi;


    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenuri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userinfouri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)->{
                    authorize.requestMatchers("/logouts").hasRole("user")
                            .requestMatchers("/api2").hasRole("admin")
                            .requestMatchers("/krmp-proxy.9rum.cc/**","/login/**","/","/test/**",",/reqlogin/**","/kauth.kakao.com/**","/kapi.kakao.com/**").permitAll()
                            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .anyRequest().authenticated();

                })
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /*.oauth2Login((login)->login
                        .loginPage("/login")

                        .userInfoEndpoint(c->c.userService(customOatuh2Service))
                        .successHandler(sucessHandler)
                        .failureHandler(failHandler)
                )*/
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c->c.authenticationEntryPoint(entryPointHandler)
                        .accessDeniedHandler(authenHandler))

                .build();





    }

}
