package com.booking.tennisbook.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // General errors
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    INVALID_REQUEST(400, "Invalid request"),
    UNAUTHORIZED(401, "Unauthorized access"),
    FORBIDDEN(403, "Forbidden access"),
    NOT_FOUND(404, "Resource not found"),

    // Authentication errors
    INVALID_CREDENTIALS(1001, "Invalid credentials"),
    EMAIL_ALREADY_EXISTS(1002, "Email already exists"),
    INVALID_TOKEN(1003, "Invalid or expired token"),

    // Booking errors
    SESSION_NOT_FOUND(2001, "Session not found"),
    SESSION_FULL(2002, "Session is full"),
    BOOKING_NOT_FOUND(2003, "Booking not found"),
    BOOKING_ALREADY_EXISTS(2004, "Booking already exists for this session"),
    INVALID_PARTICIPANTS(2005, "Invalid number of participants"),
    BOOKING_CANCELLED(2006, "Booking is already cancelled"),
    BOOKING_EXPIRED(2007, "Booking has expired"),
    SESSION_NOT_ENOUGH(2008, "Session is not enough for current participants"),
    BOOKING_FAILED(2009, "Booking failed"),

    // Payment errors
    PAYMENT_FAILED(3001, "Payment failed"),
    INVALID_PAYMENT_AMOUNT(3002, "Invalid payment amount"),
    PAYMENT_ALREADY_PROCESSED(3003, "Payment already processed"),
    PAYMENT_NOT_FOUND(3004, "Payment not found"),
    PAYMENT_ALREADY_EXISTS(3005, "Payment already exists for this booking"),
    INVALID_PAYMENT_STATUS(3006, "Invalid payment status for this operation"),
    PAYMENT_METHOD_NOT_FOUND(3007, "Payment method not found");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
} 