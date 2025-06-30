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
public class HeadersData {
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("signature")
    private String signature;
    @JsonProperty("date")
    private String date;
    @JsonProperty("client_id")
    private String clientId;
} 