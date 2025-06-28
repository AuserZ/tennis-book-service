package com.booking.tennisbook.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Data
@Table(name = "payment_methods")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class PaymentMethod {
    @Id
    private String id;

    @OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentStep> paymentSteps;

    @Column(nullable = false)
    private String methodName;

    @Column(nullable = false)
    private String accountNumber;
}
