package com.pro.bookingservice.service;

import com.pro.bookingservice.model.Booking;
import com.pro.bookingservice.repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional

    public Booking bookEvent(Long userId, Long eventId) {
        // Check if an event is already booked
        if (bookingRepository.existsByEventIdAndStatus(eventId, Booking.BookingStatus.CONFIRMED)) {
            throw new IllegalStateException("Event is already booked");
        }

        // Check if a user has already booked this event
        Optional<Booking> existingBooking = bookingRepository.findByUserIdAndEventId(userId, eventId);
        if (existingBooking.isPresent()) {
            throw new IllegalStateException("User has already booked this event");
        }

        // Create a new booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setEventId(eventId);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }
    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId);
    }


    public List<Booking> getEventBookings(Long eventId) {
        return bookingRepository.findByEventId(eventId);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
}