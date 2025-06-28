package com.booking.tennisbook.service;

import com.booking.tennisbook.dto.payment.CreatePaymentResponse;
import com.booking.tennisbook.model.Payment;
import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.model.PaymentStep;

import java.util.List;

public interface PaymentService {
    CreatePaymentResponse createPayment(Long bookingId, String paymentMethodId);
    Payment getPayment(Long paymentId);
    List<Payment> getPaymentsByBooking(Long bookingId);
    Payment refundPayment(Long paymentId);
    PaymentMethod getPaymentMethodWithSteps(String paymentMethodId);
    List<PaymentStep> getPaymentStepsByMethod(String paymentMethodId);
} 