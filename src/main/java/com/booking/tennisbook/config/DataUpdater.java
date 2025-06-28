package com.booking.tennisbook.config;

import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.model.PaymentStep;
import com.booking.tennisbook.repository.PaymentMethodRepository;
import com.booking.tennisbook.repository.PaymentStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Order(2) // Run after DataSeeder
@ConditionalOnProperty(name = "app.seeder.update-enabled", havingValue = "true", matchIfMissing = true)
public class DataUpdater implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataUpdater.class);

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentStepRepository paymentStepRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data updater...");
        
        // Update existing payment methods if needed
        updatePaymentMethods();
        
        // Add new payment methods if they don't exist
        addNewPaymentMethods();
        
        logger.info("Data updater completed!");
    }

    private void updatePaymentMethods() {
        // Example: Update BCA Transfer account number
        Optional<PaymentMethod> bcaMethod = paymentMethodRepository.findById("BCA1");
        if (bcaMethod.isPresent()) {
            PaymentMethod method = bcaMethod.get();
            if (!"1234567890".equals(method.getAccountNumber())) {
                method.setAccountNumber("1234567890");
                paymentMethodRepository.save(method);
                logger.info("Updated BCA Transfer account number");
            }
        }

        // Example: Update Credit Card steps
        updatePaymentSteps("CC1", Arrays.asList(
            "Select Credit Card payment option",
            "Enter card number",
            "Enter cardholder name",
            "Enter expiry date (MM/YY)",
            "Enter CVV/CVC code",
            "Enter billing address",
            "Review payment details",
            "Click Pay Now",
            "Enter 3D Secure password if prompted",
            "Payment completed",
            "Check email for confirmation" // New step
        ));
    }

    private void addNewPaymentMethods() {
        // Add new payment method if it doesn't exist
        if (!paymentMethodRepository.existsById("SHOPEE1")) {
            PaymentMethod shopeeMethod = createPaymentMethod("SHOPEE1", "ShopeePay", "081234567893");
            List<String> shopeeSteps = Arrays.asList(
                "Open Shopee app",
                "Login to your account",
                "Go to ShopeePay section",
                "Select Pay menu",
                "Scan QR code or enter merchant code",
                "Enter payment amount",
                "Review payment details",
                "Enter your PIN",
                "Payment successful"
            );
            createPaymentSteps(shopeeMethod, shopeeSteps);
            logger.info("Added new payment method: ShopeePay");
        }

        // Add another new payment method
        if (!paymentMethodRepository.existsById("LINKAJA1")) {
            PaymentMethod linkajaMethod = createPaymentMethod("LINKAJA1", "LinkAja", "081234567894");
            List<String> linkajaSteps = Arrays.asList(
                "Open LinkAja app",
                "Login to your account",
                "Go to Pay menu",
                "Scan QR code or enter merchant code",
                "Enter payment amount",
                "Review payment details",
                "Enter your PIN",
                "Payment successful"
            );
            createPaymentSteps(linkajaMethod, linkajaSteps);
            logger.info("Added new payment method: LinkAja");
        }
    }

    private void updatePaymentSteps(String paymentMethodId, List<String> newStepDescriptions) {
        Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findById(paymentMethodId);
        if (paymentMethod.isPresent()) {
            // Delete existing steps
            List<PaymentStep> existingSteps = paymentStepRepository.findByPaymentMethodId(paymentMethodId);
            paymentStepRepository.deleteAll(existingSteps);
            
            // Create new steps
            createPaymentSteps(paymentMethod.get(), newStepDescriptions);
            logger.info("Updated payment steps for method: " + paymentMethodId);
        }
    }

    private PaymentMethod createPaymentMethod(String id, String methodName, String accountNumber) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(id);
        paymentMethod.setMethodName(methodName);
        paymentMethod.setAccountNumber(accountNumber);
        return paymentMethodRepository.save(paymentMethod);
    }

    private void createPaymentSteps(PaymentMethod paymentMethod, List<String> stepDescriptions) {
        for (int i = 0; i < stepDescriptions.size(); i++) {
            PaymentStep step = new PaymentStep();
            step.setPaymentMethod(paymentMethod);
            step.setStepDescription(stepDescriptions.get(i));
            step.setStepNumber(String.valueOf(i + 1));
            paymentStepRepository.save(step);
        }
    }
} 