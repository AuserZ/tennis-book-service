package com.booking.tennisbook.controller;

import com.booking.tennisbook.dto.payment.CreatePaymentResponse;
import com.booking.tennisbook.dto.payment.PaymentDokuResponse;
import com.booking.tennisbook.dto.payment.PaymentRequestBody;
import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.model.Booking;
import com.booking.tennisbook.model.Payment;
import com.booking.tennisbook.repository.BookingRepository;
import com.booking.tennisbook.repository.PaymentRepository;
import com.booking.tennisbook.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    // @PostMapping("/booking")
    // public ResponseEntity<CreatePaymentResponse> createPayment(PaymentRequestBody requestBody) {

    //     CreatePaymentResponse payment = paymentService.createPayment(requestBody.getBookingId(),
    //             requestBody.getPaymentMethodId());
    //     return ResponseEntity.ok(payment);
    // }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<Payment>> getPaymentsByBooking(@PathVariable Long bookingId) {
        List<Payment> payments = paymentService.getPaymentsByBooking(bookingId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Payment> refundPayment(@PathVariable Long paymentId) {
        Payment payment = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/new")
    public ResponseEntity<?> newPaymentDoku(@RequestBody PaymentRequestBody request) {
        logger.info("[START] New Payment Doku");
        PaymentDokuResponse payment = paymentService.createPaymentDoku(request.getBookingId());

        if (isEmpty(payment))
            throw new BusinessException(ErrorCode.PAYMENT_FAILED);

        logger.info("[END] New Payment Doku");

        return ResponseEntity.ok(payment);
    }

    @PostMapping("/doku/notification")
    public ResponseEntity<Map<String, Object>> handleDokuNotification(@RequestBody Map<String, Object> payload, @RequestHeader Map<String, String> headers) {
        return ResponseEntity.ok(paymentService.handleDokuNotification(payload, headers));
    }
}