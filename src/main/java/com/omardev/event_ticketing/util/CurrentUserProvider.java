package com.omardev.event_ticketing.util;

import com.omardev.event_ticketing.entity.User;
import com.omardev.event_ticketing.exception.ApiException;
import com.omardev.event_ticketing.exception.UserNotFoundException;
import com.omardev.event_ticketing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Utility class for authentication-related operations
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    /**
     * Get current authenticated user ID from JWT
     */
    public String getCurrentUserId() {

        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            throw new ApiException("User is not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new ApiException("Invalid authentication token");
        }

        return jwt.getSubject();
    }

    /**
     * Get the current authenticated user from DB
     */
    public User getCurrentUser() {

        String userId = getCurrentUserId();

        return userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}