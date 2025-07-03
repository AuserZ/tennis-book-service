package com.booking.tennisbook.service;

import com.booking.tennisbook.dto.payment.PaymentDokuResponse;

import java.util.Map;

public interface PaymentService {
    PaymentDokuResponse createPaymentDoku(Long bookingId);
    Map<String, Object> handleDokuNotification(Map<String, Object> payload, Map<String, String> headers);
} 