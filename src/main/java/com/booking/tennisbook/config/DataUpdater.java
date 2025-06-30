package com.booking.tennisbook.config;

import com.booking.tennisbook.enums.PaymentEnums;
import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.repository.PaymentMethodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(2) // Run after DataSeeder
@ConditionalOnProperty(name = "app.seeder.update-enabled", havingValue = "true", matchIfMissing = true)
public class DataUpdater implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataUpdater.class);

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data updater...");
        
        // Update existing payment methods if needed
        updatePaymentMethods();
        
        logger.info("Data updater completed!");
    }

    private void updatePaymentMethods() {
        // Example: Update BCA Virtual Account display name
        Optional<PaymentMethod> bcaMethod = paymentMethodRepository.findById(PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BCA.getValue());
        if (bcaMethod.isPresent()) {
            PaymentMethod method = bcaMethod.get();
            if (!"BCA Virtual Account".equals(method.getDisplayName())) {
                method.setDisplayName("BCA Virtual Account");
                method.setDescription("Pay using BCA Virtual Account");
                paymentMethodRepository.save(method);
                logger.info("Updated BCA Virtual Account details");
            }
        }
    }
} 