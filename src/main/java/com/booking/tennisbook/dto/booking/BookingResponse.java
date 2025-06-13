package com.booking.tennisbook.dto.booking;

import com.booking.tennisbook.dto.coach.CoachDto;
import com.booking.tennisbook.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long sessionId;
    private CoachDto coach;
    private LocalDate date;
    private LocalTime time;
    private Booking.BookingStatus status;
    private BigDecimal totalPrice;
} 