package com.booking.tennisbook.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseData {
    private OrderData order;
    private PaymentData payment;
    private AdditionalInfo additionalInfo;
    private Long uuid;
    private HeadersData headers;
} 