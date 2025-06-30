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
public class OriginData {
    @JsonProperty("product")
    private String product;
    @JsonProperty("system")
    private String system;
    @JsonProperty("apiFormat")
    private String apiFormat;
    @JsonProperty("source")
    private String source;
} 