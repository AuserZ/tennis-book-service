package com.booking.tennisbook.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "payment_steps")
@EntityListeners(AuditingEntityListener.class)
public class PaymentStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String stepDescription;

    @Column(nullable = false)
    private String stepNumber;

}
