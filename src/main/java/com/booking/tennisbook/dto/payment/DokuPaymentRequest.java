package com.booking.tennisbook.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class DokuPaymentRequest {
    private OrderDoku order;
    private PaymentDoku payment;
    private CustomerDetails customer;
}
