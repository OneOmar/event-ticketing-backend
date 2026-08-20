package com.omardev.event_ticketing.exception;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException(String keycloakId) {
        super("User not found with Keycloak ID: " + keycloakId);
    }
}