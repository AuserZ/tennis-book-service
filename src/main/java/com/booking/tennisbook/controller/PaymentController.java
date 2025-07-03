package com.booking.tennisbook.controller;

import com.booking.tennisbook.dto.payment.PaymentDokuResponse;
import com.booking.tennisbook.dto.payment.PaymentRequestBody;
import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger logger = Logger.getLogger(PaymentController.class.getName());

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
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