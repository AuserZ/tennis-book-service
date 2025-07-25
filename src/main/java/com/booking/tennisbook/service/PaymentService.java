package com.booking.tennisbook.service;

import com.booking.tennisbook.model.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(Long bookingId, String paymentMethod);
    Payment processPayment(Long paymentId);
    Payment getPayment(Long paymentId);
    List<Payment> getPaymentsByBooking(Long bookingId);
    Payment refundPayment(Long paymentId);
} 