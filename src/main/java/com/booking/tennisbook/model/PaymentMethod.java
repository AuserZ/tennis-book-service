package com.booking.tennisbook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Data
@Table(name = "payment_methods")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class PaymentMethod {
    @Id
    private String id;

    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PaymentStep> paymentSteps = new ArrayList<>();

    @Column(nullable = false)
    private String methodName;

    @Column(nullable = false)
    private String accountNumber;

    // Convenience methods for managing payment steps
    public void addPaymentStep(PaymentStep paymentStep) {
        paymentSteps.add(paymentStep);
        paymentStep.setPaymentMethod(this);
    }

    public void removePaymentStep(PaymentStep paymentStep) {
        paymentSteps.remove(paymentStep);
        paymentStep.setPaymentMethod(null);
    }
}
