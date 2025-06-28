package com.booking.tennisbook.repository;

import com.booking.tennisbook.model.PaymentStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentStepRepository extends JpaRepository<PaymentStep, Long> {
    List<PaymentStep> findByPaymentId(Long id);
}
