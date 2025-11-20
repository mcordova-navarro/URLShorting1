package ru.cft.template.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static ru.cft.template.constants.Endpoints.*;

@Slf4j
@Configuration
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthProvider authProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(login -> login.usernameParameter("username"))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer ->
                        configurer.authenticationEntryPoint(((request, response, authException) -> {
                                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                    response.getWriter().write("Unauthorized");
                                }))
                                .accessDeniedHandler(((request, response, accessDeniedException) -> {
                                    response.setStatus(HttpStatus.FORBIDDEN.value());
                                    response.getWriter().write("Unauthorized");
                                }))
                )
                .authorizeHttpRequests(configurer ->
                        configurer.requestMatchers(
                                        SESSIONS_URL + REFRESH,
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/actuator/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, USERS_URL).authenticated()
                                .requestMatchers(HttpMethod.GET, USERS_URL).authenticated()
                                .requestMatchers(HttpMethod.GET, SESSIONS_URL).authenticated()
                                .requestMatchers(HttpMethod.POST, SESSIONS_URL).permitAll()
                                .requestMatchers(HttpMethod.POST, USERS_URL).permitAll()
                                .anyRequest().authenticated()
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authProvider);

        return httpSecurity.build();
    }
}
