package com.booking.tennisbook.dto.payment;

import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestBody {
    @NonNull
    private Long bookingId;
}
