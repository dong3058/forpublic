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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                .cors(c->c.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize)->{
                    authorize.requestMatchers("/logouts","/api1","/code/**","/mypage/**").hasRole("user")

                            .requestMatchers("/login/**","/","/test/**","/reqlogin/**",tokenuri,userinfouri,"http://krmp-proxy.9rum.cc:3128/**").permitAll()
                            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .anyRequest().permitAll();

                })
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /*.oauth2Login((login)->login
                        .loginPage("/login")

                        .userInfoEndpoint(c->c.userService(customOatuh2Service))
                        .successHandler(sucessHandler)
                        .failureHandler(failHandler)
                )*/
                .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(c->c.authenticationEntryPoint(entryPointHandler)
                        .accessDeniedHandler(authenHandler))

                .build();





    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("https://k56733b335962a.user-app.krampoline.com");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
