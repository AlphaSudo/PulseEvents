package com.pro.bookingservice.service;

import com.pro.bookingservice.model.Booking;
import com.pro.bookingservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingSecurityService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingSecurityService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public boolean isOwner(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            return false;
        }

        // This assumes userId and username are equivalent, or you have a way to map between them
        return booking.getUserId().toString().equals(username);
    }
}