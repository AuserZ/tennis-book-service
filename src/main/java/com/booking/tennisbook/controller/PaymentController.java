package com.booking.tennisbook.controller;

import com.booking.tennisbook.model.Payment;
import com.booking.tennisbook.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<Payment> createPayment(
            @PathVariable Long bookingId,
            @RequestParam String paymentMethod) {
        Payment payment = paymentService.createPayment(bookingId, paymentMethod);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{paymentId}/process")
    public ResponseEntity<Payment> processPayment(@PathVariable Long paymentId) {
        Payment payment = paymentService.processPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

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
} 