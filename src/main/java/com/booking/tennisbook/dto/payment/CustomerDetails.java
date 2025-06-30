package com.booking.tennisbook.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDetails {
    private Long id;
    private String name;
    private String email;
}
