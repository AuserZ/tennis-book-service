package com.booking.tennisbook.dto.booking;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Number of participants is required")
    @Min(value = 1, message = "Number of participants must be at least 1")
    private Integer participants;
} 