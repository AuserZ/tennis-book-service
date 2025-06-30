package com.booking.tennisbook.config;

import com.booking.tennisbook.enums.PaymentEnums;
import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.repository.PaymentMethodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Run first
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data seeder...");
        
        // Check if data already exists
        if (paymentMethodRepository.count() == 0) {
            seedPaymentMethods();
            logger.info("Payment methods seeded successfully!");
        } else {
            logger.info("Data already exists, skipping seeder.");
        }
    }

    private void seedPaymentMethods() {
        // Create BCA Virtual Account Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BCA.getValue(), 
            "BCA Virtual Account", 
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BCA,
            "BCA Virtual Account",
            "Pay using BCA Virtual Account"
        );

        // Create Mandiri Virtual Account Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BANK_MANDIRI.getValue(), 
            "Mandiri Virtual Account", 
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BANK_MANDIRI,
            "Mandiri Virtual Account",
            "Pay using Mandiri Virtual Account"
        );

        // Create Credit Card Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.CREDIT_CARD.getValue(), 
            "Credit Card", 
            PaymentEnums.PaymentMethodType.CREDIT_CARD,
            "Credit Card",
            "Pay using Credit Card"
        );

        // Create OVO E-Money Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.EMONEY_OVO.getValue(), 
            "OVO E-Money", 
            PaymentEnums.PaymentMethodType.EMONEY_OVO,
            "OVO E-Money",
            "Pay using OVO E-Money"
        );

        // Create DANA E-Money Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.EMONEY_DANA.getValue(), 
            "DANA E-Money", 
            PaymentEnums.PaymentMethodType.EMONEY_DANA,
            "DANA E-Money",
            "Pay using DANA E-Money"
        );

        // Create ShopeePay E-Money Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.EMONEY_SHOPEEPAY.getValue(), 
            "ShopeePay E-Money", 
            PaymentEnums.PaymentMethodType.EMONEY_SHOPEEPAY,
            "ShopeePay E-Money",
            "Pay using ShopeePay E-Money"
        );

        // Create QRIS Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.QRIS.getValue(), 
            "QRIS", 
            PaymentEnums.PaymentMethodType.QRIS,
            "QRIS",
            "Pay using QRIS"
        );

        // Create BRI Virtual Account Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BRI.getValue(), 
            "BRI Virtual Account", 
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BRI,
            "BRI Virtual Account",
            "Pay using BRI Virtual Account"
        );

        // Create BNI Virtual Account Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BNI.getValue(), 
            "BNI Virtual Account", 
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BNI,
            "BNI Virtual Account",
            "Pay using BNI Virtual Account"
        );

        // Create LinkAja E-Money Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.EMONEY_LINKAJA.getValue(), 
            "LinkAja E-Money", 
            PaymentEnums.PaymentMethodType.EMONEY_LINKAJA,
            "LinkAja E-Money",
            "Pay using LinkAja E-Money"
        );

        // Create Permata Virtual Account Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BANK_PERMATA.getValue(), 
            "Permata Virtual Account", 
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BANK_PERMATA,
            "Permata Virtual Account",
            "Pay using Permata Virtual Account"
        );

        // Create CIMB Virtual Account Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BANK_CIMB.getValue(), 
            "CIMB Virtual Account", 
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BANK_CIMB,
            "CIMB Virtual Account",
            "Pay using CIMB Virtual Account"
        );

        // Create Danamon Virtual Account Payment Method
        createPaymentMethod(
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BANK_DANAMON.getValue(), 
            "Danamon Virtual Account", 
            PaymentEnums.PaymentMethodType.VIRTUAL_ACCOUNT_BANK_DANAMON,
            "Danamon Virtual Account",
            "Pay using Danamon Virtual Account"
        );
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