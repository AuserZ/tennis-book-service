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
public class PaymentData {
    private List<String> paymentMethodTypes;
    private Integer paymentDueDate;
    private String tokenId;
    private String url;
    private String expiredDate;
} 