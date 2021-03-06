package com.mpnp.baechelin.config.security;

import com.mpnp.baechelin.common.properties.AppProperties;
import com.mpnp.baechelin.common.properties.CorsProperties;
import com.mpnp.baechelin.login.oauth.entity.RoleType;
import com.mpnp.baechelin.login.jwt.exception.RestAuthenticationEntryPoint;
import com.mpnp.baechelin.login.jwt.filter.TokenAuthenticationFilter;
import com.mpnp.baechelin.login.oauth.handler.OAuth2AuthenticationFailureHandler;
import com.mpnp.baechelin.login.oauth.handler.OAuth2AuthenticationSuccessHandler;
import com.mpnp.baechelin.login.jwt.handler.TokenAccessDeniedHandler;
import com.mpnp.baechelin.login.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.mpnp.baechelin.login.oauth.service.CustomOAuth2UserService;
import com.mpnp.baechelin.login.jwt.AuthTokenProvider;
import com.mpnp.baechelin.login.jwt.repository.UserRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;
    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final CustomOAuth2UserService oAuth2UserService;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final UserRefreshTokenRepository userRefreshTokenRepository;


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors() // cors ??????
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session??? ???????????? ?????? ????????? ????????? stateless ?????? ??????
                .and()
                .csrf().disable() // csrf ?????? ??????
                .formLogin().disable() // ?????????????????? ????????? ????????? ????????? formLogin ??????
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // ????????? ????????? ???, ?????? ????????? ????????? ?????? ?????? 401 ?????? ??????
                .accessDeniedHandler(tokenAccessDeniedHandler)
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // cors ?????? ??????
                .antMatchers("/review", "/api/bookmark", "/store/register", "/userinfo", "/user/logout").hasAnyAuthority(RoleType.USER.getCode(), RoleType.ADMIN.getCode())
                .antMatchers("/admin/**").hasAnyAuthority(RoleType.ADMIN.getCode())
                .antMatchers("/**").permitAll() // ??? ??? ????????? ?????? ??????
                .anyRequest().authenticated() // ?????? ?????? ?????? ????????? ????????? ????????????
                .and()
                .oauth2Login() // auth2 ????????? ?????????
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/*/oauth2/code/*")
                .and()
                .userInfoEndpoint()
                .userService(oAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler()) // ?????? ?????? ??? ??????
                .failureHandler(oAuth2AuthenticationFailureHandler()); // ?????? ?????? ??? ??????

        // tokenAuthenticationFilter ??? UsernamePasswordAuthenticationFilter ?????? ?????? ??????????????? ?????? ?????????
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * auth ????????? ??????
     * */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
     * security ?????? ???, ????????? ????????? ??????
     * */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * ?????? ?????? ??????
     * */
    @Bean
    public  TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    /*
     * ?????? ?????? ?????? Repository
     * ?????? ????????? ?????? ?????? ????????? ??? ??????.
     * */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /*
     * Oauth ?????? ?????? ?????????
     * */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                tokenProvider,
                appProperties,
                userRefreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    /*
     * Oauth ?????? ?????? ?????????
     * */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    /*
     * Cors ??????
     * */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
        corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
        corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(corsConfig.getMaxAge());


        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();
        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
        return corsConfigSource;
    }
}