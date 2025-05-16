package com.pro.authenticationservice.exception;



/**
 * Thrown when attempting to register a username that already exists.
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username '" + username + "' is already taken");
    }
}