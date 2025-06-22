package com.booking.tennisbook.service.impl;

import com.booking.tennisbook.controller.BookingController;
import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.repository.BookingRepository;
import com.booking.tennisbook.repository.SessionRepository;
import com.booking.tennisbook.service.SessionService;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.booking.tennisbook.dto.booking.BookingRequest;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.model.Booking;
import com.booking.tennisbook.model.User;
import com.booking.tennisbook.repository.UserRepository;
import com.booking.tennisbook.service.BookingService;

import java.awt.print.Book;
import java.math.BigDecimal;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private SessionService sessionService;


    @Override
    public Booking createBooking(BookingRequest bookingRequest) {
        logger.info("[START] createBooking: sessionId={}, participants={}", bookingRequest.getSessionId(), bookingRequest.getParticipants());
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.info("[STEP] Fetching user by email: {}", userEmail);
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("[ERROR] User not found with email: {}", userEmail);
                        return new BusinessException(ErrorCode.NOT_FOUND);
                    });

            logger.info("[STEP] Fetching session by id: {}", bookingRequest.getSessionId());
            Session session = sessionRepository.findById(bookingRequest.getSessionId())
                    .orElseThrow(() -> {
                        logger.error("[ERROR] Session not found with ID: {}", bookingRequest.getSessionId());
                        return new BusinessException(ErrorCode.SESSION_NOT_FOUND);
                    });

            logger.info("[STEP] Checking if session is full (current: {}, max: {})", session.getCurrentParticipants(), session.getMaxParticipants());
            if (NumberUtils.compare(session.getCurrentParticipants(), session.getMaxParticipants()) >= 0) {
                logger.warn("[ERROR] Session is full. Current participants: {}, Max participants: {}",
                        session.getCurrentParticipants(), session.getMaxParticipants());
                throw new BusinessException(ErrorCode.SESSION_FULL);
            }

            logger.info("[STEP] Calculating total price");
            BigDecimal totalPrice = session.getPricePerPerson()
                    .multiply(BigDecimal.valueOf(bookingRequest.getParticipants()));

            logger.info("[STEP] Creating Booking entity");
            Booking booking = new Booking();
            booking.setSession(session);
            booking.setUser(user);
            booking.setParticipants(bookingRequest.getParticipants());
            booking.setTotalPrice(totalPrice);
            booking.setStatus(Booking.BookingStatus.PENDING);

            logger.info("[STEP] Saving booking to repository");
            booking = bookingRepository.save(booking);

            logger.info("[END] Booking created successfully: bookingId={}", booking.getId());
            return booking;
        } catch (Exception e) {
            logger.error("[ERROR] Exception in createBooking: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
            throw e;
        }
    }

}
