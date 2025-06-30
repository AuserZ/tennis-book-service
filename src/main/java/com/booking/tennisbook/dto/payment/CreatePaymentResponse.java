package com.booking.tennisbook.dto.payment;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreatePaymentResponse {
    @NonNull
    private Long paymentId;

    @NonNull
    private String status;

    @NonNull
    private String message;

    @NonNull
    private String invoiceNumber;
}
