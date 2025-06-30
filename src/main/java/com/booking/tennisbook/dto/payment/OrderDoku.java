package com.booking.tennisbook.dto.payment;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderDoku {
    private BigDecimal amount;
    private String invoiceNumber;
    // private List<LineItemsDoku> lineItems;
    private String currency;
    
}
