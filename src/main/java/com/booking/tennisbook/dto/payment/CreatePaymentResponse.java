package com.booking.tennisbook.dto.payment;

import com.booking.tennisbook.model.PaymentStep;
import lombok.*;

import java.util.List;

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
    private List<PaymentStep> paymentSteps;
}
