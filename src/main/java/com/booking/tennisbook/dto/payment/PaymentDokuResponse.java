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
public class PaymentDokuResponse {
    private List<String> message;
    private PaymentResponseData response;
}
