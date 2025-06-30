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
        
        // Add new payment methods if they don't exist
        addNewPaymentMethods();
        
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

        // Example: Update Credit Card description
        Optional<PaymentMethod> ccMethod = paymentMethodRepository.findById(PaymentEnums.PaymentMethodType.CREDIT_CARD.getValue());
        if (ccMethod.isPresent()) {
            PaymentMethod method = ccMethod.get();
            if (!"Pay using Credit Card".equals(method.getDescription())) {
                method.setDescription("Pay using Credit Card");
                paymentMethodRepository.save(method);
                logger.info("Updated Credit Card description");
            }
        }
    }

    private void addNewPaymentMethods() {
        // Add new payment method if it doesn't exist
        if (!paymentMethodRepository.existsById(PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_MAYBANK.getValue())) {
            createPaymentMethod(
                PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_MAYBANK.getValue(),
                "Maybank Virtual Account",
                PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_MAYBANK,
                "Maybank Virtual Account",
                "Pay using Maybank Virtual Account"
            );
            logger.info("Added new payment method: Maybank Virtual Account");
        }

        // Add another new payment method
        if (!paymentMethodRepository.existsById(PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_SINARMAS.getValue())) {
            createPaymentMethod(
                PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_SINARMAS.getValue(),
                "Sinarmas Virtual Account",
                PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_SINARMAS,
                "Sinarmas Virtual Account",
                "Pay using Sinarmas Virtual Account"
            );
            logger.info("Added new payment method: Sinarmas Virtual Account");
        }

        // Add more virtual accounts
        if (!paymentMethodRepository.existsById(PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BNC.getValue())) {
            createPaymentMethod(
                PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BNC.getValue(),
                "BNC Virtual Account",
                PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BNC,
                "BNC Virtual Account",
                "Pay using BNC Virtual Account"
            );
            logger.info("Added new payment method: BNC Virtual Account");
        }

        if (!paymentMethodRepository.existsById(PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BTN.getValue())) {
            createPaymentMethod(
                PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BTN.getValue(),
                "BTN Virtual Account",
                PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BTN,
                "BTN Virtual Account",
                "Pay using BTN Virtual Account"
            );
            logger.info("Added new payment method: BTN Virtual Account");
        }

        // Add e-money methods
        if (!paymentMethodRepository.existsById(PaymentEnums.PaymentMethodType.EMONEY_DOKU.getValue())) {
            createPaymentMethod(
                PaymentEnums.PaymentMethodType.EMONEY_DOKU.getValue(),
                "Doku E-Money",
                PaymentEnums.PaymentMethodType.EMONEY_DOKU,
                "Doku E-Money",
                "Pay using Doku E-Money"
            );
            logger.info("Added new payment method: Doku E-Money");
        }

        if (!paymentMethodRepository.existsById(PaymentEnums.PaymentMethodType.EMONEY_SHOPEE_PAY.getValue())) {
            createPaymentMethod(
                PaymentEnums.PaymentMethodType.EMONEY_SHOPEE_PAY.getValue(),
                "ShopeePay E-Money",
                PaymentEnums.PaymentMethodType.EMONEY_SHOPEE_PAY,
                "ShopeePay E-Money",
                "Pay using ShopeePay E-Money"
            );
            logger.info("Added new payment method: ShopeePay E-Money");
        }

        // Add peer-to-peer methods
        if (!paymentMethodRepository.existsById(PaymentEnums.PaymentMethodType.PEER_TO_PEER_KREDIVO.getValue())) {
            createPaymentMethod(
                PaymentEnums.PaymentMethodType.PEER_TO_PEER_KREDIVO.getValue(),
                "Kredivo PayLater",
                PaymentEnums.PaymentMethodType.PEER_TO_PEER_KREDIVO,
                "Kredivo PayLater",
                "Pay using Kredivo PayLater"
            );
            logger.info("Added new payment method: Kredivo PayLater");
        }

        if (!paymentMethodRepository.existsById(PaymentEnums.PaymentMethodType.PEER_TO_PEER_INDODANA.getValue())) {
            createPaymentMethod(
                PaymentEnums.PaymentMethodType.PEER_TO_PEER_INDODANA.getValue(),
                "Indodana PayLater",
                PaymentEnums.PaymentMethodType.PEER_TO_PEER_INDODANA,
                "Indodana PayLater",
                "Pay using Indodana PayLater"
            );
            logger.info("Added new payment method: Indodana PayLater");
        }
    }

    private PaymentMethod createPaymentMethod(String id, String methodName, PaymentEnums.PaymentMethodType paymentMethodType, String displayName, String description) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(id);
        paymentMethod.setMethodName(methodName);
        paymentMethod.setPaymentMethodType(paymentMethodType);
        paymentMethod.setDisplayName(displayName);
        paymentMethod.setDescription(description);
        paymentMethod.setIsActive(true);
        return paymentMethodRepository.save(paymentMethod);
    }
} 