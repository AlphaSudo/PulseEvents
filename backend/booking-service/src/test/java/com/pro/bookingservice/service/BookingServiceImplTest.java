package com.pro.bookingservice.service;

import com.pro.bookingservice.model.Booking;
import com.pro.bookingservice.repository.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;

    @Test
    @DisplayName("bookEvent should save and return a booking")
    void bookEvent_shouldSaveAndReturnBooking() {
        // Arrange
        Long userId = 1L;
        Long eventId = 2L;

        when(bookingRepository.existsByEventIdAndStatus(eventId, Booking.BookingStatus.CONFIRMED))
                .thenReturn(false);
        when(bookingRepository.findByUserIdAndEventId(userId, eventId))
                .thenReturn(Optional.empty());

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setUserId(userId);
        savedBooking.setEventId(eventId);
        savedBooking.setBookingTime(LocalDateTime.now());
        savedBooking.setStatus(Booking.BookingStatus.CONFIRMED);

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        // Act
        Booking result = bookingService.bookEvent(userId, eventId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getEventId()).isEqualTo(eventId);
        assertThat(result.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);

        verify(bookingRepository).save(bookingCaptor.capture());
        Booking capturedBooking = bookingCaptor.getValue();
        assertThat(capturedBooking.getUserId()).isEqualTo(userId);
        assertThat(capturedBooking.getEventId()).isEqualTo(eventId);
        assertThat(capturedBooking.getBookingTime()).isNotNull();
    }

    @Test
    @DisplayName("bookEvent should throw exception when event is already booked")
    void bookEvent_shouldThrowException_whenEventAlreadyBooked() {
        // Arrange
        Long userId = 1L;
        Long eventId = 2L;

        when(bookingRepository.existsByEventIdAndStatus(eventId, Booking.BookingStatus.CONFIRMED))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.bookEvent(userId, eventId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Event is already booked");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("bookEvent should throw exception when user already booked the event")
    void bookEvent_shouldThrowException_whenUserAlreadyBookedEvent() {
        // Arrange
        Long userId = 1L;
        Long eventId = 2L;

        when(bookingRepository.existsByEventIdAndStatus(eventId, Booking.BookingStatus.CONFIRMED))
                .thenReturn(false);

        Booking existingBooking = new Booking();
        existingBooking.setId(1L);
        existingBooking.setUserId(userId);
        existingBooking.setEventId(eventId);

        when(bookingRepository.findByUserIdAndEventId(userId, eventId))
                .thenReturn(Optional.of(existingBooking));

        // Act & Assert
        assertThatThrownBy(() -> bookingService.bookEvent(userId, eventId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User has already booked this event");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("getUserBookings should return list of user bookings")
    void getUserBookings_shouldReturnUserBookings() {
        // Arrange
        Long userId = 1L;
        List<Booking> bookings = Arrays.asList(
                createBooking(1L, userId, 2L, Booking.BookingStatus.CONFIRMED),
                createBooking(2L, userId, 3L, Booking.BookingStatus.PENDING)
        );

        when(bookingRepository.findByUserId(userId)).thenReturn(bookings);

        // Act
        List<Booking> result = bookingService.getUserBookings(userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(0).getEventId()).isEqualTo(2L);
        assertThat(result.get(0).getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getUserId()).isEqualTo(userId);
        assertThat(result.get(1).getEventId()).isEqualTo(3L);
        assertThat(result.get(1).getStatus()).isEqualTo(Booking.BookingStatus.PENDING);

        verify(bookingRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("getEventBookings should return list of event bookings")
    void getEventBookings_shouldReturnEventBookings() {
        // Arrange
        Long eventId = 1L;
        List<Booking> bookings = Arrays.asList(
                createBooking(1L, 2L, eventId, Booking.BookingStatus.CONFIRMED),
                createBooking(2L, 3L, eventId, Booking.BookingStatus.PENDING)
        );

        when(bookingRepository.findByEventId(eventId)).thenReturn(bookings);

        // Act
        List<Booking> result = bookingService.getEventBookings(eventId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getUserId()).isEqualTo(2L);
        assertThat(result.get(0).getEventId()).isEqualTo(eventId);

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getUserId()).isEqualTo(3L);
        assertThat(result.get(1).getEventId()).isEqualTo(eventId);

        verify(bookingRepository).findByEventId(eventId);
    }

    @Test
    @DisplayName("getBooking should return booking when exists")
    void getBooking_shouldReturnBooking_whenExists() {
        // Arrange
        Long bookingId = 1L;
        Booking booking = createBooking(bookingId, 1L, 2L, Booking.BookingStatus.CONFIRMED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // Act
        Booking result = bookingService.getBooking(bookingId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookingId);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEventId()).isEqualTo(2L);
        assertThat(result.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("getBooking should throw exception when booking doesn't exist")
    void getBooking_shouldThrowException_whenNotExists() {
        // Arrange
        Long bookingId = 999L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingService.getBooking(bookingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Booking not found");

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("cancelBooking should update status to CANCELLED")
    void cancelBooking_shouldUpdateStatusToCancelled() {
        // Arrange
        Long bookingId = 1L;
        Booking booking = createBooking(bookingId, 1L, 2L, Booking.BookingStatus.CONFIRMED);
        Booking cancelledBooking = createBooking(bookingId, 1L, 2L, Booking.BookingStatus.CANCELLED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(cancelledBooking);

        // Act
        Booking result = bookingService.cancelBooking(bookingId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Booking.BookingStatus.CANCELLED);

        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(booking);
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