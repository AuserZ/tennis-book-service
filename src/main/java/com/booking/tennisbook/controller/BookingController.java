package com.booking.tennisbook.controller;

import com.booking.tennisbook.dto.booking.BookingRequest;
import com.booking.tennisbook.dto.booking.BookingResponse;
import com.booking.tennisbook.dto.coach.CoachDto;
import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.model.Booking;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.model.User;
import com.booking.tennisbook.repository.BookingRepository;
import com.booking.tennisbook.repository.SessionRepository;
import com.booking.tennisbook.repository.UserRepository;
import com.booking.tennisbook.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingRepository bookingRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    @PostMapping("/newBookSession")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        logger.info("Received booking request for session ID: {} with {} participants",
                bookingRequest.getSessionId(), bookingRequest.getParticipants());

        try {
            Booking resultNewBooking = bookingService.createBooking(bookingRequest);

            logger.info("Successfully created booking with ID: {} for session ID: {}",
                    resultNewBooking.getId(), resultNewBooking.getId());

            return ResponseEntity.ok(mapToBookingResponse(resultNewBooking));
        } catch (BusinessException e) {
            logger.error("Business error while creating booking: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while creating booking: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        logger.info("Received request to fetch user's bookings");
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", userEmail);
                        return new BusinessException(ErrorCode.NOT_FOUND);
                    });

            List<Booking> bookings = bookingRepository.findByUserId(user.getId());
            List<BookingResponse> response = bookings.stream()
                    .map(this::mapToBookingResponse)
                    .collect(Collectors.toList());

            logger.info("Found {} bookings for user ID: {}", bookings.size(), user.getId());
            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            logger.error("Business error while fetching bookings: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while fetching bookings: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        logger.info("Received request to cancel booking ID: {}", bookingId);
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> {
                        logger.error("User not found with email: {}", userEmail);
                        return new BusinessException(ErrorCode.NOT_FOUND);
                    });

            return bookingRepository.findById(bookingId)
                    .filter(booking -> booking.getUser().getId().equals(user.getId()))
                    .map(booking -> {
                        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
                            logger.warn("Booking ID: {} is already cancelled", bookingId);
                            throw new BusinessException(ErrorCode.BOOKING_CANCELLED);
                        }
                        booking.setStatus(Booking.BookingStatus.CANCELLED);
                        bookingRepository.save(booking);
                        logger.info("Successfully cancelled booking ID: {}", bookingId);
                        return ResponseEntity.ok().<Void>build();
                    })
                    .orElseGet(() -> {
                        logger.warn("Booking not found with ID: {}", bookingId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (BusinessException e) {
            logger.error("Business error while cancelling booking: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while cancelling booking: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .sessionId(booking.getSession().getId())
                .coach(CoachDto.builder()
                        .id(booking.getSession().getCoach().getId())
                        .name(booking.getSession().getCoach().getName())
                        .phoneNumber(booking.getSession().getCoach().getPhoneNumber())
                        .build())
                .date(booking.getSession().getDate())
                .time(booking.getSession().getStartTime())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .build();
    }
}