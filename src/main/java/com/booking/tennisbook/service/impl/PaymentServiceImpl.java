package com.booking.tennisbook.service.impl;

import com.booking.tennisbook.dto.payment.CreatePaymentResponse;
import com.booking.tennisbook.dto.payment.DokuPaymentRequest;
import com.booking.tennisbook.dto.payment.OrderDoku;
import com.booking.tennisbook.dto.payment.PaymentDokuResponse;
import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.model.Booking;
import com.booking.tennisbook.model.Payment;
import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.model.User;
import com.booking.tennisbook.repository.*;
import com.booking.tennisbook.service.PaymentService;
import com.booking.tennisbook.service.SessionService;
import com.booking.tennisbook.util.PaymentUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private PaymentUtil paymentUtil;

    @Autowired
    private final SessionService sessionService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository,
            SessionService sessionService, PaymentMethodRepository paymentMethodRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.sessionService = sessionService;
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(Long bookingId, String paymentMethodId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));

        if (paymentRepository.existsByBookingIdAndStatus(bookingId, Payment.PaymentStatus.COMPLETED)) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Payment processedPayment = processPayment(payment.getId(), booking);

        if (processedPayment == null) {
            logger.error("Payment processing failed for booking ID: {}", bookingId);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }

        CreatePaymentResponse processedPaymentResponse = new CreatePaymentResponse();

        processedPaymentResponse.setPaymentId(processedPayment.getId());
        processedPaymentResponse.setMessage(
                processedPayment.getStatus() == Payment.PaymentStatus.COMPLETED ? "Payment processed successfully"
                        : "Payment processing failed");
        processedPaymentResponse.setStatus(String.valueOf(processedPayment.getStatus()));

        return processedPaymentResponse;
    }

    @Transactional
    public Payment processPayment(Long paymentId, Booking booking) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        // update session participants
        Session session = sessionService.updateSessionParticipants(booking.getSession(), booking.getParticipants());
        if (session == null) {
            logger.error("Failed to update session participants for session ID: {}", booking.getSession());
            throw new BusinessException(ErrorCode.SESSION_NOT_ENOUGH);
        }

        // Here you would integrate with a real payment gateway
        // For now, we'll just simulate a successful payment
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());

        // Update booking status
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Override
    public List<Payment> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    @Override
    @Transactional
    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        // Here you would integrate with a real payment gateway for refund
        // For now, we'll just simulate a successful refund
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());

        // Update booking status
        Booking booking = payment.getBooking();
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }

    @Override
    public PaymentMethod getPaymentMethod(String paymentMethodId) {
        return paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND));
    }

    @Override
    public PaymentDokuResponse createPaymentDoku(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOKING_NOT_FOUND));

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", userEmail);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });

        // Build Payment Request to doku
        DokuPaymentRequest paymentRequest = paymentUtil.buildDokuRequest(booking, user);

        if(isEmpty(paymentRequest))
                throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        
        // Request Payment
        PaymentDokuResponse response = paymentUtil.processPayment(paymentRequest);

        if(isEmpty(response))
                throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        

        return response;
    }

}