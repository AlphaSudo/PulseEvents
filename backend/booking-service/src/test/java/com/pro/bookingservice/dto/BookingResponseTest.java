package com.pro.bookingservice.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingResponseTest {

    @Test
    @DisplayName("BookingResponse constructor should set all fields correctly")
    void constructor_shouldSetAllFieldsCorrectly() {
        // Arrange
        Long id = 1L;
        Long userId = 2L;
        Long resourceId = 3L;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        String status = "CONFIRMED";

        // Act
        BookingResponse response = new BookingResponse(
                id, userId, resourceId, startTime, endTime, status);

        // Assert
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getResourceId()).isEqualTo(resourceId);
        assertThat(response.getStartTime()).isEqualTo(startTime);
        assertThat(response.getEndTime()).isEqualTo(endTime);
        assertThat(response.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("BookingResponse setters should correctly update values")
    void setters_shouldUpdateValues() {
        // Arrange
        BookingResponse response = new BookingResponse();
        Long id = 1L;
        Long userId = 2L;
        Long resourceId = 3L;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(2);
        String status = "CONFIRMED";

        // Act
        response.setId(id);
        response.setUserId(userId);
        response.setResourceId(resourceId);
        response.setStartTime(startTime);
        response.setEndTime(endTime);
        response.setStatus(status);

        // Assert
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getResourceId()).isEqualTo(resourceId);
        assertThat(response.getStartTime()).isEqualTo(startTime);
        assertThat(response.getEndTime()).isEqualTo(endTime);
        assertThat(response.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("Default constructor should create empty BookingResponse")
    void defaultConstructor_shouldCreateEmptyResponse() {
        // Act
        BookingResponse response = new BookingResponse();

        // Assert
        assertThat(response.getId()).isNull();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getResourceId()).isNull();
        assertThat(response.getStartTime()).isNull();
        assertThat(response.getEndTime()).isNull();
        assertThat(response.getStatus()).isNull();
    }
}