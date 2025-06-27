package com.booking.tennisbook.service;

import com.booking.tennisbook.model.Payment;

import java.util.List;

public interface PaymentService {
    Payment createPayment(Long bookingId, Long paymentMethod);
    Payment getPayment(Long paymentId);
    List<Payment> getPaymentsByBooking(Long bookingId);
    Payment refundPayment(Long paymentId);
} 