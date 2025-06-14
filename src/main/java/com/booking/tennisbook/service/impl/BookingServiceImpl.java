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
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", userEmail);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });

        Session session = sessionRepository.findById(bookingRequest.getSessionId())
                .orElseThrow(() -> {
                    logger.error("Session not found with ID: {}", bookingRequest.getSessionId());
                    return new BusinessException(ErrorCode.SESSION_NOT_FOUND);
                });

        // Check if session is full
        if (NumberUtils.compare(session.getCurrentParticipants(), session.getMaxParticipants()) >= 0) {
            logger.warn("Session is full. Current participants: {}, Max participants: {}",
                    session.getCurrentParticipants(), session.getMaxParticipants());
            throw new BusinessException(ErrorCode.SESSION_FULL);
        }

        // update session participants
        session = sessionService.updateSessionParticipants(session, bookingRequest.getParticipants());
        if (session == null) {
            logger.error("Failed to update session participants for session ID: {}", bookingRequest.getSessionId());
            throw new BusinessException(ErrorCode.SESSION_NOT_ENOUGH);
        }

        Booking booking = new Booking();
        booking.setSession(session);
        booking.setUser(user);
        booking.setParticipants(bookingRequest.getParticipants());
        booking.setTotalPrice(session.getPricePerPerson()
                .multiply(java.math.BigDecimal.valueOf(bookingRequest.getParticipants())));
        booking.setTotalAmount(booking.getTotalPrice());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);

        return booking;
    }

}
