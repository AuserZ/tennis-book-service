package com.booking.tennisbook.repository;

import com.booking.tennisbook.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByTransactionId(String transactionId);
    boolean existsByBookingIdAndStatus(Long bookingId, Payment.PaymentStatus status);
} 