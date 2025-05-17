package com.pro.bookingservice.repository;

import com.pro.bookingservice.model.Booking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    @DisplayName("findByUserId should return bookings for a specific user")
    void findByUserId_shouldReturnUserBookings() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;

        Booking booking1 = createBooking(userId1, 101L, Booking.BookingStatus.CONFIRMED);
        Booking booking2 = createBooking(userId1, 102L, Booking.BookingStatus.PENDING);
        Booking booking3 = createBooking(userId2, 103L, Booking.BookingStatus.CONFIRMED);

        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        entityManager.flush();

        // Act
        List<Booking> userBookings = bookingRepository.findByUserId(userId1);

        // Assert
        assertThat(userBookings).hasSize(2);
        assertThat(userBookings).extracting(Booking::getUserId)
                .containsOnly(userId1);
    }

    @Test
    @DisplayName("findByEventId should return bookings for a specific event")
    void findByEventId_shouldReturnEventBookings() {
        // Arrange
        Long eventId = 101L;

        Booking booking1 = createBooking(1L, eventId, Booking.BookingStatus.CONFIRMED);
        Booking booking2 = createBooking(2L, eventId, Booking.BookingStatus.PENDING);
        Booking booking3 = createBooking(3L, 102L, Booking.BookingStatus.CONFIRMED);

        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        entityManager.flush();

        // Act
        List<Booking> eventBookings = bookingRepository.findByEventId(eventId);

        // Assert
        assertThat(eventBookings).hasSize(2);
        assertThat(eventBookings).extracting(Booking::getEventId)
                .containsOnly(eventId);
    }



    @Test
    @DisplayName("existsByEventIdAndStatus should return true when booking exists")
    void existsByEventIdAndStatus_shouldReturnTrue_whenExists() {
        // Arrange
        Long eventId = 101L;
        Booking.BookingStatus status = Booking.BookingStatus.CONFIRMED;

        Booking booking = createBooking(1L, eventId, status);
        entityManager.persist(booking);
        entityManager.flush();

        // Act
        boolean exists = bookingRepository.existsByEventIdAndStatus(eventId, status);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEventIdAndStatus should return false when booking doesn't exist")
    void existsByEventIdAndStatus_shouldReturnFalse_whenNotExists() {
        // Arrange
        Long eventId = 101L;
        Booking.BookingStatus status = Booking.BookingStatus.CONFIRMED;

        // No matching booking in database

        // Act
        boolean exists = bookingRepository.existsByEventIdAndStatus(eventId, status);

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByUserIdAndEventId should return booking when exists")
    void findByUserIdAndEventId_shouldReturnBooking_whenExists() {
        // Arrange
        Long userId = 1L;
        Long eventId = 101L;

        Booking booking = createBooking(userId, eventId, Booking.BookingStatus.CONFIRMED);
        entityManager.persist(booking);
        entityManager.flush();

        // Act
        Optional<Booking> result = bookingRepository.findByUserIdAndEventId(userId, eventId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(userId);
        assertThat(result.get().getEventId()).isEqualTo(eventId);
    }

    private Booking createBooking(Long userId, Long eventId, Booking.BookingStatus status) {
        LocalDateTime now = LocalDateTime.of(2023, 5, 15, 10, 0);

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setEventId(eventId);
        booking.setBookingTime(now);
        booking.setStatus(status);
        return booking;
    }
}