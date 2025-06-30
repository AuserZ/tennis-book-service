package com.booking.tennisbook.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderData {
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("invoice_number")
    private String invoiceNumber;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("session_id")
    private String sessionId;
} 