package com.booking.tennisbook.dto.session;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SessionDateRequest {
    @NotNull(message = "Date is required")
    private LocalDate date;
} 