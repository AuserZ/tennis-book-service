package com.booking.tennisbook.config;

import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.model.PaymentStep;
import com.booking.tennisbook.repository.PaymentMethodRepository;
import com.booking.tennisbook.repository.PaymentStepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Order(1) // Run first
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PaymentStepRepository paymentStepRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data seeder...");
        
        // Check if data already exists
        if (paymentMethodRepository.count() == 0) {
            seedPaymentMethods();
            logger.info("Payment methods and steps seeded successfully!");
        } else {
            logger.info("Data already exists, skipping seeder.");
        }
    }

    private void seedPaymentMethods() {
        // Create BCA Transfer Payment Method
        PaymentMethod bcaMethod = createPaymentMethod("BCA1", "BCA Transfer", "1234567890");
        List<String> bcaSteps = Arrays.asList(
            "Open your BCA mobile app",
            "Login to your account",
            "Go to Transfer menu",
            "Select BCA Virtual Account",
            "Enter Virtual Account number: 1234567890",
            "Enter amount to transfer",
            "Review and confirm transfer",
            "Enter your PIN",
            "Transfer completed"
        );
        createPaymentSteps(bcaMethod, bcaSteps);

        // Create Mandiri Transfer Payment Method
        PaymentMethod mandiriMethod = createPaymentMethod("MDR1", "Mandiri Transfer", "0987654321");
        List<String> mandiriSteps = Arrays.asList(
            "Open Mandiri Online app",
            "Login with your credentials",
            "Navigate to Transfer menu",
            "Select Transfer to Bank",
            "Choose BCA as destination bank",
            "Enter account number: 1234567890",
            "Enter transfer amount",
            "Review transfer details",
            "Enter your PIN",
            "Transfer successful"
        );
        createPaymentSteps(mandiriMethod, mandiriSteps);

        // Create Credit Card Payment Method
        PaymentMethod ccMethod = createPaymentMethod("CC1", "Credit Card", "4111111111111111");
        List<String> ccSteps = Arrays.asList(
            "Select Credit Card payment option",
            "Enter card number",
            "Enter cardholder name",
            "Enter expiry date (MM/YY)",
            "Enter CVV/CVC code",
            "Enter billing address",
            "Review payment details",
            "Click Pay Now",
            "Enter 3D Secure password if prompted",
            "Payment completed"
        );
        createPaymentSteps(ccMethod, ccSteps);

        // Create OVO Wallet Payment Method
        PaymentMethod ovoMethod = createPaymentMethod("OVO1", "OVO Wallet", "081234567890");
        List<String> ovoSteps = Arrays.asList(
            "Open OVO app",
            "Login to your account",
            "Go to Pay menu",
            "Scan QR code or enter merchant code",
            "Enter payment amount",
            "Review payment details",
            "Enter your PIN",
            "Payment successful"
        );
        createPaymentSteps(ovoMethod, ovoSteps);

        // Create GoPay Payment Method
        PaymentMethod gopayMethod = createPaymentMethod("GOPAY1", "GoPay", "081234567891");
        List<String> gopaySteps = Arrays.asList(
            "Open GoJek app",
            "Login to your account",
            "Go to GoPay section",
            "Select Pay menu",
            "Scan QR code or enter merchant code",
            "Enter payment amount",
            "Review payment details",
            "Enter your PIN",
            "Payment completed"
        );
        createPaymentSteps(gopayMethod, gopaySteps);

        // Create DANA Wallet Payment Method
        PaymentMethod danaMethod = createPaymentMethod("DANA1", "DANA Wallet", "081234567892");
        List<String> danaSteps = Arrays.asList(
            "Open DANA app",
            "Login to your account",
            "Go to Pay menu",
            "Scan QR code or enter merchant code",
            "Enter payment amount",
            "Review payment details",
            "Enter your PIN",
            "Payment successful"
        );
        createPaymentSteps(danaMethod, danaSteps);
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