package com.booking.tennisbook.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDoku {
    private Integer payment_due_date;
    private String type;
    private List<String> payment_method_types;
}
