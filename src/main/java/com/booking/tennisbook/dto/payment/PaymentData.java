package com.booking.tennisbook.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("payment_method_types")
    private List<String> paymentMethodTypes;
    @JsonProperty("payment_due_date")
    private Integer paymentDueDate;
    @JsonProperty("token_id")
    private String tokenId;
    @JsonProperty("url")
    private String url;
    @JsonProperty("expired_date")
    private String expiredDate;
    @JsonProperty("expired_datetime")
    private String expiredDatetime;
    @JsonProperty("type")
    private String type;
} 