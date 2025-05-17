package com.pro.bookingservice.controller;

import com.pro.bookingservice.config.JwtAuthenticationFilter;
import com.pro.bookingservice.config.TestConfig;
import com.pro.bookingservice.config.TestSecurityConfig;
import com.pro.bookingservice.model.Booking;
import com.pro.bookingservice.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)


class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TestRestTemplate restTemplate;

    @MockitoBean
    private BookingService bookingService;

    @Test
    @DisplayName("POST /bookings - Create a new booking")
    void createBooking_shouldReturnCreatedBooking() throws Exception {
        // Arrange
        Long userId = 1L;
        Long eventId = 2L;
        
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUserId(userId);
        booking.setEventId(eventId);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        when(bookingService.bookEvent(anyLong(), anyLong())).thenReturn(booking);

        // Act & Assert
        mockMvc.perform(post("/bookings")
                        .param("userId", userId.toString())
                        .param("eventId", eventId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.eventId").value(eventId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /bookings - Retrieve all bookings")
    void getAllBookings_shouldReturnListOfBookings() throws Exception {
        // Arrange
        List<Booking> bookings = Arrays.asList(
                createBooking(1L, 1L, 2L, Booking.BookingStatus.CONFIRMED),
                createBooking(2L, 3L, 1L, Booking.BookingStatus.PENDING)
        );

        when(bookingService.getUserBookings(null)).thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$[1].status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /bookings/{id} - Retrieve booking by ID")
    void getBookingById_shouldReturnBooking() throws Exception {
        // Arrange
        Long bookingId = 1L;
        Booking booking = createBooking(
                bookingId, 1L, 2L, Booking.BookingStatus.CONFIRMED
        );

        when(bookingService.getBooking(bookingId)).thenReturn(booking);

        // Act & Assert
        mockMvc.perform(get("/bookings/{id}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
    
    @Test
    @DisplayName("GET /bookings/user/{userId} - Retrieve bookings by user ID")
    void getBookingsByUser_shouldReturnUserBookings() throws Exception {
        // Arrange
        Long userId = 1L;
        List<Booking> bookings = Arrays.asList(
                createBooking(1L, userId, 2L, Booking.BookingStatus.CONFIRMED),
                createBooking(2L, userId, 3L, Booking.BookingStatus.PENDING)
        );

        when(bookingService.getUserBookings(userId)).thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/bookings/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].userId").value(userId));
    }
    
    @Test
    @DisplayName("GET /bookings/event/{eventId} - Retrieve bookings by event ID")
    void getBookingsByEvent_shouldReturnEventBookings() throws Exception {
        // Arrange
        Long eventId = 2L;
        List<Booking> bookings = Arrays.asList(
                createBooking(1L, 1L, eventId, Booking.BookingStatus.CONFIRMED),
                createBooking(2L, 3L, eventId, Booking.BookingStatus.PENDING)
        );

        when(bookingService.getEventBookings(eventId)).thenReturn(bookings);

        // Act & Assert
        mockMvc.perform(get("/bookings/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].eventId").value(eventId))
                .andExpect(jsonPath("$[1].eventId").value(eventId));
    }
    
    @Test
    @DisplayName("DELETE /bookings/{id} - Cancel booking")
    void cancelBooking_shouldReturnCancelledBooking() throws Exception {
        // Arrange
        Long bookingId = 1L;
        Booking booking = createBooking(
                bookingId, 1L, 2L, Booking.BookingStatus.CANCELLED
        );

        when(bookingService.cancelBooking(bookingId)).thenReturn(booking);

        // Act & Assert
        mockMvc.perform(delete("/bookings/{id}", bookingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
    
    @Test
    @DisplayName("POST /bookings - Return conflict when event already booked")
    void createBooking_shouldReturnConflict_whenEventAlreadyBooked() throws Exception {
        // Arrange
        long userId = 1L;
        long eventId = 2L;
        
        when(bookingService.bookEvent(anyLong(), anyLong()))
            .thenThrow(new IllegalStateException("Event is already booked"));

        // Act & Assert
        mockMvc.perform(post("/bookings")
                        .param("userId", Long.toString(userId))
                        .param("eventId", Long.toString(eventId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
    
    @Test
    @DisplayName("GET /bookings/{id} - Return not found when booking doesn't exist")
    void getBookingById_shouldReturnNotFound_whenBookingDoesntExist() throws Exception {
        // Arrange
        Long bookingId = 999L;
        
        when(bookingService.getBooking(bookingId))
            .thenThrow(new IllegalArgumentException("Booking not found"));

        // Act & Assert
        mockMvc.perform(get("/bookings/{id}", bookingId))
                .andExpect(status().isNotFound());
    }

    private Booking createBooking(Long id, Long userId, Long eventId, Booking.BookingStatus status) {
        LocalDateTime now = LocalDateTime.of(2023, 5, 15, 10, 0);
        
        Booking booking = new Booking();
        booking.setId(id);
        booking.setUserId(userId);
        booking.setEventId(eventId);
        booking.setBookingTime(now);
        booking.setStatus(status);
        return booking;
    }
}