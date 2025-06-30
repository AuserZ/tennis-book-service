package com.booking.tennisbook.model;

import com.booking.tennisbook.enums.PaymentEnums;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Table(name = "payment_methods")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class PaymentMethod {
    @Id
    private String id;

    @Column(nullable = false)
    private String methodName;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method_type", nullable = false)
    private PaymentEnums.PaymentMethodType paymentMethodType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "description")
    private String description;
}
