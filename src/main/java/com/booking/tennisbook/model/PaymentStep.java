package com.booking.tennisbook.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PaymentMethod paymentMethod;

    @Column(name = "step_number", nullable = false)
    private String stepNumber;

    @Column(name = "step_description", nullable = false)
    private String stepDescription;
} 