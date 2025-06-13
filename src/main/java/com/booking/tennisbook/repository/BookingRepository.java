package com.booking.tennisbook.repository;

import com.booking.tennisbook.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    boolean existsBySessionIdAndUserId(Long sessionId, Long userId);
} 