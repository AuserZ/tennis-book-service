package com.booking.tennisbook.config;

import com.booking.tennisbook.enums.PaymentEnums;
import com.booking.tennisbook.model.PaymentMethod;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.model.Coach;
import com.booking.tennisbook.model.TennisField;
import com.booking.tennisbook.repository.PaymentMethodRepository;
import com.booking.tennisbook.repository.SessionRepository;
import com.booking.tennisbook.repository.CoachRepository;
import com.booking.tennisbook.repository.TennisFieldRepository;
import com.booking.tennisbook.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@Order(1) // Run first
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private TennisFieldRepository tennisFieldRepository;

    @Autowired
    private UserRepository userRepository;

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

        // Session data
        if (sessionRepository.count() == 0) {
            seedSessions();
            logger.info("Sessions seeded successfully!");
        } else {
            logger.info("Session data already exists, skipping seeder.");
        }

        // User data
        if (userRepository.count() == 0) {
            logger.info("User seeded successfully!");
        } else {
            logger.info("User data already exists, skipping seeder.");
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

    private void seedSessions() {
        // Create or fetch a coach
        Coach coach = coachRepository.findAll().stream().findFirst().orElseGet(() -> {
            Coach c = new Coach();
            c.setName("John Doe");
            c.setPhoneNumber("08123456789");
            c.setBio("Professional tennis coach");
            return coachRepository.save(c);
        });
        // Create or fetch a tennis field
        TennisField field = tennisFieldRepository.findAll().stream().findFirst().orElseGet(() -> {
            TennisField f = new TennisField();
            f.setName("Central Court");
            f.setLocation("Jakarta");
            f.setPhotoUrl("https://images.unsplash.com/photo-1566241121793-3e25f3586e43?q=80&w=1074&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
            return tennisFieldRepository.save(f);
        });
        // Seed a few sessions
        for (int i = 0; i < 30; i++) {
            Session session = new Session();
            session.setCoach(coach);
            session.setTennisField(field);
            session.setDate(LocalDate.now().plusDays(i));
            session.setStartTime(LocalTime.of(8, 0)); // Always 08:00
            session.setEndTime(LocalTime.of(10, 0));  // Always 10:00
            session.setMaxParticipants(4);
            session.setCurrentParticipants(0);
            session.setPricePerPerson(BigDecimal.valueOf(150000 + i * 50000));
            session.setDescription("Morning tennis session " + (i + 1));
            session.setType("Public");
            session.setStatus(Session.SessionStatus.ACTIVE);
            sessionRepository.save(session);
        }
    }
} 