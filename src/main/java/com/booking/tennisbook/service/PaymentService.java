package com.booking.tennisbook.service;

import com.booking.tennisbook.dto.payment.CreatePaymentResponse;
import com.booking.tennisbook.dto.payment.PaymentDokuResponse;
import com.booking.tennisbook.model.Payment;
import com.booking.tennisbook.model.PaymentMethod;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    CreatePaymentResponse createPayment(Long bookingId, String paymentMethodId);
    Payment getPayment(Long paymentId);
    List<Payment> getPaymentsByBooking(Long bookingId);
    Payment refundPayment(Long paymentId);
    PaymentMethod getPaymentMethod(String paymentMethodId);
    PaymentDokuResponse createPaymentDoku(Long bookingId);
    Map<String, Object> handleDokuNotification(Map<String, Object> payload, Map<String, String> headers);
} 