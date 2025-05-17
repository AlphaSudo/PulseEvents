package com.pro.bookingservice;

import com.pro.bookingservice.config.JwtAuthenticationFilter;
import com.pro.bookingservice.dto.BookingRequest;
import com.pro.bookingservice.dto.BookingResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("End-to-end booking creation test")
    void createBooking_shouldCreateAndReturnBooking() {
        // Arrange
        String baseUrl = "http://localhost:" + port + "/booking-api";
        BookingRequest request = new BookingRequest(
                1L, 2L,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2)
        );

        // Act
        ResponseEntity<BookingResponse> response = restTemplate.postForEntity(
                baseUrl + "/bookings", request, BookingResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUserId()).isEqualTo(1L);
        assertThat(response.getBody().getResourceId()).isEqualTo(2L);
    }
}