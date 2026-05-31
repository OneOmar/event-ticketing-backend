package com.omardev.event_ticketing.security.config;

import com.omardev.event_ticketing.security.filter.UserProvisioningFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserProvisioningFilter userProvisioningFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http

                // Disable CSRF for REST APIs
                .csrf(csrf -> csrf.disable())

                // Stateless JWT authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Route authorization
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers("/api/public/**")
                        .permitAll()

                        // Secure everything else
                        .anyRequest().authenticated()
                )

                // OAuth2 JWT Resource Server
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                )

                // Register provisioning filter
                .addFilterAfter(
                        userProvisioningFilter,
                        BearerTokenAuthenticationFilter.class
                );

        return http.build();
    }
}