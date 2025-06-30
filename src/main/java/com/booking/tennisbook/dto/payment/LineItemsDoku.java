package com.booking.tennisbook.dto.payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Builder;
import lombok.Builder.Default;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineItemsDoku {
    private Long sessionId;
    private String coach;
    private String tennisField;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate date;
    private String sessionType;
    private Integer quantity;
    private BigDecimal totalAmount;
    
    @Default
    private String category = "service";
}
