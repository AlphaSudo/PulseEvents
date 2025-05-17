package com.pro.bookingservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BookingRequestTest {

    @Test
    @DisplayName("Valid BookingRequest should pass validation")
    void validBookingRequest_shouldPassValidation() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        BookingRequest request = new BookingRequest(
                1L,
                2L,
                now.plusMinutes(10),
                now.plusHours(2)
        );

        // Act
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Test
    @DisplayName("BookingRequest with null userId should fail validation")
    void bookingRequestWithNullUserId_shouldFailValidation() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        BookingRequest request = new BookingRequest(
                null,
                2L,
                now.plusMinutes(10),
                now.plusHours(2)
        );

        // Act & Assert
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("User ID is required");
        }
    }

    @Test
    @DisplayName("BookingRequest with past startTime should fail validation")
    void bookingRequestWithPastStartTime_shouldFailValidation() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        BookingRequest request = new BookingRequest(
                1L,
                2L,
                now.minusHours(1), // Pastime
                now.plusHours(2)
        );

        // Act & Assert
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("Start time must be in the present or future");
        }
    }

    @Test
    @DisplayName("BookingRequest with past endTime should fail validation")
    void bookingRequestWithPastEndTime_shouldFailValidation() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        BookingRequest request = new BookingRequest(
                1L,
                2L,
                now.plusMinutes(10),
                now.minusHours(1) // Pastime
        );

        // Act & Assert
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("End time must be in the future");
        }
    }

    @Test
    @DisplayName("BookingRequest setters should correctly update values")
    void bookingRequestSetters_shouldUpdateValues() {
        // Arrange
        BookingRequest request = new BookingRequest();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusMinutes(15);
        LocalDateTime endTime = now.plusHours(2);

        // Act
        request.setUserId(5L);
        request.setResourceId(10L);
        request.setStartTime(startTime);
        request.setEndTime(endTime);

        // Assert
        assertThat(request.getUserId()).isEqualTo(5L);
        assertThat(request.getResourceId()).isEqualTo(10L);
        assertThat(request.getStartTime()).isEqualTo(startTime);
        assertThat(request.getEndTime()).isEqualTo(endTime);
    }
}