package com.booking.tennisbook.service;

import com.booking.tennisbook.dto.payment.CreatePaymentResponse;
import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.model.Booking;
import com.booking.tennisbook.model.Payment;
import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.repository.*;
import com.booking.tennisbook.service.SessionService;
import com.booking.tennisbook.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentStepRepository paymentStepRepository;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPayment_BookingNotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                paymentService.createPayment(1L, 1L)
        );

        assertEquals(ErrorCode.BOOKING_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createPayment_PaymentMethodNotFound_ThrowsException() {
        Booking booking = new Booking();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                paymentService.createPayment(1L, 1L)
        );
        assertEquals(ErrorCode.PAYMENT_METHOD_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createPayment_PaymentAlreadyExists_ThrowsException() {
        Booking booking = new Booking();
        PaymentMethod paymentMethod = new PaymentMethod();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
        when(paymentRepository.existsByBookingIdAndStatus(1L, Payment.PaymentStatus.COMPLETED)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                paymentService.createPayment(1L, 1L)
        );

        assertEquals(ErrorCode.PAYMENT_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void createPayment_SuccessfulPayment_ReturnsResponse() {
        Booking booking = new Booking();
        Session session = new Session();
        session.setId(2L);

        booking.setId(1L);
        booking.setTotalPrice(BigDecimal.valueOf(100));
        booking.setSession(session);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId("BCA1");

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(paymentMethod));
        when(paymentRepository.existsByBookingIdAndStatus(1L, Payment.PaymentStatus.COMPLETED)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(sessionService.updateSessionParticipants(session, 1)).thenReturn(null);
        when(paymentStepRepository.findByPaymentMethodId(1L)).thenReturn(List.of());

        CreatePaymentResponse response = paymentService.createPayment(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getPaymentId());
        assertEquals("Payment processed successfully", response.getMessage());
    }
}