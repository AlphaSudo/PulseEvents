package com.pro.authenticationservice.dto;



/**
 * Simple error payload returned in @ControllerAdvice handlers.
 */
public record ApiError(
        String code,
        String message
) { }