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
    private Integer amount;
    private String invoice_number;
    private String currency;
    private String callback_url;
    private String callback_url_cancel;
    private String callback_url_result;
    
}
