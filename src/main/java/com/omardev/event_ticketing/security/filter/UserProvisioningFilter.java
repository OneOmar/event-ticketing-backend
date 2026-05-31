package com.omardev.event_ticketing.security.filter;

import com.omardev.event_ticketing.entity.User;
import com.omardev.event_ticketing.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        // Process authenticated JWT users only
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof Jwt jwt) {

            String keycloakId = jwt.getSubject();

            // Skip provisioning if user already exists
            if (!userRepository.existsByKeycloakId(keycloakId)) {

                User user = new User();

                user.setKeycloakId(keycloakId);
                user.setEmail(jwt.getClaimAsString("email"));
                user.setFirstName(jwt.getClaimAsString("given_name"));
                user.setLastName(jwt.getClaimAsString("family_name"));
                user.setEnabled(true);

                userRepository.save(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}