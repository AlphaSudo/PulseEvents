package com.pro.bookingservice.repository;

import com.pro.bookingservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByEventId(Long eventId);

    Optional<Booking> findByUserIdAndEventId(Long userId, Long eventId);

    boolean existsByEventIdAndStatus(Long eventId, Booking.BookingStatus status);
}