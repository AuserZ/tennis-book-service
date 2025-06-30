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
import com.booking.tennisbook.model.DokuNotificationLog;
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
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final PaymentUtil paymentUtil;
    private final DokuNotificationLogRepository dokuNotificationLogRepository;

    @Autowired
    private final SessionService sessionService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository,
            SessionService sessionService, PaymentMethodRepository paymentMethodRepository, UserRepository userRepository, PaymentUtil paymentUtil, DokuNotificationLogRepository dokuNotificationLogRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.sessionService = sessionService;
        this.paymentMethodRepository = paymentMethodRepository;
        this.userRepository = userRepository;
        this.paymentUtil = paymentUtil;
        this.dokuNotificationLogRepository = dokuNotificationLogRepository;
    }

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(Long bookingId, String paymentMethodId) {
        logger.info("[START] Creating payment for booking ID: {}, payment method: {}", bookingId, paymentMethodId);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking not found with ID: {}", bookingId);
                    return new BusinessException(ErrorCode.BOOKING_NOT_FOUND);
                });
        logger.debug("Found booking: ID={}, TotalPrice={}, Status={}", 
                    booking.getId(), booking.getTotalPrice(), booking.getStatus());

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> {
                    logger.error("Payment method not found with ID: {}", paymentMethodId);
                    return new BusinessException(ErrorCode.PAYMENT_METHOD_NOT_FOUND);
                });
        logger.debug("Found payment method: ID={}, Name={}, Type={}", 
                    paymentMethod.getId(), paymentMethod.getMethodName(), paymentMethod.getPaymentMethodType());

        if (paymentRepository.existsByBookingIdAndStatus(bookingId, Payment.PaymentStatus.COMPLETED)) {
            logger.error("Payment already exists for booking ID: {}", bookingId);
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        logger.info("Creating new payment record");
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        logger.info("Payment record created with ID: {}, Transaction ID: {}", payment.getId(), payment.getTransactionId());

        logger.info("Processing payment with ID: {}", payment.getId());
        Payment processedPayment = processPayment(payment.getId(), booking);

        if (processedPayment == null) {
            logger.error("Payment processing failed for booking ID: {}", bookingId);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }

        logger.info("Building payment response");
        CreatePaymentResponse processedPaymentResponse = new CreatePaymentResponse();

        processedPaymentResponse.setPaymentId(processedPayment.getId());
        processedPaymentResponse.setMessage(
                processedPayment.getStatus() == Payment.PaymentStatus.COMPLETED ? "Payment processed successfully"
                        : "Payment processing failed");
        processedPaymentResponse.setStatus(String.valueOf(processedPayment.getStatus()));

        logger.info("[END] Payment created successfully - Payment ID: {}, Status: {}, Message: {}", 
                   processedPaymentResponse.getPaymentId(), processedPaymentResponse.getStatus(), 
                   processedPaymentResponse.getMessage());

        return processedPaymentResponse;
    }

    @Transactional
    public Payment processPayment(Long paymentId, Booking booking) {
        logger.info("[START] Processing payment with ID: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment not found with ID: {}", paymentId);
                    return new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
                });

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            logger.error("Invalid payment status: {} for payment ID: {}", payment.getStatus(), paymentId);
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        logger.info("Updating session participants for session ID: {}", booking.getSession().getId());
        // update session participants
        Session session = sessionService.updateSessionParticipants(booking.getSession(), booking.getParticipants());
        if (session == null) {
            logger.error("Failed to update session participants for session ID: {}", booking.getSession().getId());
            throw new BusinessException(ErrorCode.SESSION_NOT_ENOUGH);
        }
        logger.info("Session participants updated successfully - Current participants: {}", session.getCurrentParticipants());

        logger.info("Simulating payment gateway integration");
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setProcessedAt(LocalDateTime.now());

        // Update booking status
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        logger.info("Booking status updated to CONFIRMED for booking ID: {}", booking.getId());

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("[END] Payment processed successfully - Payment ID: {}, Status: {}, Processed at: {}", 
                   savedPayment.getId(), savedPayment.getStatus(), savedPayment.getProcessedAt());

        return savedPayment;
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
        logger.info("[START] Creating DOKU payment for booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking not found with ID: {}", bookingId);
                    return new BusinessException(ErrorCode.BOOKING_NOT_FOUND);
                });
        logger.debug("Found booking: ID={}, TotalPrice={}, Session={}", 
                    booking.getId(), booking.getTotalPrice(), booking.getSession().getId());

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Getting user details for email: {}", userEmail);
        
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", userEmail);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });
        logger.debug("Found user: ID={}, Name={}, Email={}", user.getId(), user.getName(), user.getEmail());

        logger.info("Building DOKU payment request");
        // Build Payment Request to doku
        DokuPaymentRequest paymentRequest = paymentUtil.buildDokuRequest(booking, user);

        if(isEmpty(paymentRequest)) {
            logger.error("Failed to build DOKU payment request for booking ID: {}", bookingId);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }
        logger.debug("DOKU payment request built successfully");

        // Set invoice number in booking before calling DOKU
        String invoiceNumber = paymentRequest.getOrder().getInvoice_number();
        booking.setInvoiceNumber(invoiceNumber);
        bookingRepository.save(booking);
        logger.info("Set invoice number {} in booking {}", invoiceNumber, booking.getId());
        
        logger.info("Processing DOKU payment request");
        // Request Payment
        PaymentDokuResponse response = paymentUtil.processPaymentCheckout(paymentRequest);

        if(isEmpty(response)) {
            logger.error("DOKU payment processing failed for booking ID: {}", bookingId);
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);
        }
        
        logger.info("[END] DOKU payment created successfully for booking ID: {}", bookingId);
        logger.debug("DOKU payment response: {}", response);

        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> handleDokuNotification(Map<String, Object> payload, Map<String, String> headers) {
        logger.info("Received DOKU notification: " + payload);
        String requestBody = payload.toString();
        String responseBody = null;
        String status = "FAILED";
        String errorMessage = null;
        try {
            // Support both SNAP and Non-SNAP (VA) notification formats
            String invoiceNumber = null;
            String transactionStatus = null;
            String amount = null;
            String paymentMethod = null;
            String paymentDate = null;

            // Non-SNAP: has 'transaction' object with 'status', SNAP: has 'transaction_status' at root
            if (payload.containsKey("transaction")) {
                // Non-SNAP format
                Map<String, Object> transaction = (Map<String, Object>) payload.get("transaction");
                transactionStatus = transaction != null ? (String) transaction.get("status") : null;
                paymentDate = transaction != null ? (String) transaction.get("date") : null;
                Map<String, Object> order = (Map<String, Object>) payload.get("order");
                invoiceNumber = order != null ? (String) order.get("invoice_number") : null;
                Object amountObj = order != null ? order.get("amount") : null;
                amount = amountObj != null ? amountObj.toString() : null;
                paymentMethod = payload.containsKey("payment_method") ? (String) payload.get("payment_method") : null;
            } else {
                // SNAP format (default)
                Map<String, Object> order = (Map<String, Object>) payload.get("order");
                invoiceNumber = order != null ? (String) order.get("invoice_number") : null;
                transactionStatus = (String) payload.get("transaction_status");
                Object amountObj = order != null ? order.get("amount") : null;
                amount = amountObj != null ? amountObj.toString() : null;
                paymentMethod = (String) payload.get("payment_method");
                paymentDate = (String) payload.get("payment_date");
            }

            String signature = headers.getOrDefault("signature", null);

            logger.info("Parsed notification - invoice_number: " + invoiceNumber + ", transaction_status: " + transactionStatus + ", amount: " + amount + ", payment_method: " + paymentMethod + ", payment_date: " + paymentDate);

            // (Optional) Validate signature if present
            if (signature != null) {
                // TODO: Implement signature validation if required by DOKU
                logger.info("Signature provided: " + signature);
            }

            // Use invoice number to find booking and session
            Booking booking = bookingRepository.findByInvoiceNumber(invoiceNumber).orElse(null);
            if (booking != null) {
                // Update session participants if transaction is successful
                if (transactionStatus != null && transactionStatus.equalsIgnoreCase("SUCCESS")) {
                    Session session = booking.getSession();
                    if (session != null) {
                        int newCurrent = session.getCurrentParticipants() + booking.getParticipants();
                        session.setCurrentParticipants(newCurrent);
                        logger.info("Session {} participants updated to {}", session.getId(), newCurrent);
                    }
                    booking.setStatus(Booking.BookingStatus.CONFIRMED);
                    bookingRepository.save(booking);
                    logger.info("Booking {} marked as CONFIRMED", booking.getId());
                }
                status = transactionStatus != null && transactionStatus.equalsIgnoreCase("SUCCESS") ? "SUCCESS" : "FAILED";
            } else {
                logger.warn("No booking found for invoice_number: " + invoiceNumber);
                errorMessage = "No booking found for invoice_number: " + invoiceNumber;
            }
            responseBody = "{\"status\":true,\"responseCode\":\"00\",\"responseMessage\":\"Notification received\"}";
        } catch (Exception e) {
            errorMessage = e.getMessage();
            logger.error("Error handling DOKU notification", e);
            responseBody = "{\"status\":false,\"responseCode\":\"99\",\"responseMessage\":\"Error: " + errorMessage + "\"}";
        } finally {
            DokuNotificationLog log = new DokuNotificationLog();
            log.setRequestBody(requestBody);
            log.setResponseBody(responseBody);
            log.setStatus(status);
            log.setErrorMessage(errorMessage);
            log.setInvoiceNumber(payload.containsKey("order") ? ((Map<String, Object>)payload.get("order")).get("invoice_number").toString() : null);
            dokuNotificationLogRepository.save(log);
        }
        // Return the required response
        return Map.of(
                "status", status.equals("SUCCESS"),
                "responseCode", status.equals("SUCCESS") ? "00" : "99",
                "responseMessage", status.equals("SUCCESS") ? "Notification received" : (errorMessage != null ? errorMessage : "Notification failed")
        );
    }

}