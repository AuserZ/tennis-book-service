package com.booking.tennisbook.controller;

import com.booking.tennisbook.dto.session.SessionAvailibilityRequest;
import com.booking.tennisbook.dto.session.SessionAvailibilityResponse;
import com.booking.tennisbook.dto.session.SessionByTypeRequest;
import com.booking.tennisbook.dto.session.SessionDto;
import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.repository.SessionRepository;
import com.booking.tennisbook.service.SessionService;
import com.booking.tennisbook.service.impl.SessionServiceImpl;
import jakarta.persistence.Access;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private final SessionRepository sessionRepository;
    private final SessionService sessionService;

    public SessionController(SessionRepository sessionRepository, SessionService sessionService) {
        this.sessionRepository = sessionRepository;
        this.sessionService = sessionService;
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> getAllSessions() {
        logger.info("Received request to fetch all sessions");
        try {
            List<Session> sessions = sessionRepository.findAll();
            logger.info("Successfully retrieved {} sessions from database", sessions.size());

            List<SessionDto> sessionDtos = sessions.stream()
                    .map(SessionDto::fromEntity)
                    .collect(Collectors.toList());

            logger.info("Successfully converted sessions to DTOs");
            return ResponseEntity.ok(sessionDtos);
        } catch (Exception e) {
            logger.error("Error fetching sessions: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/get-session-by-type")
    public ResponseEntity<List<SessionDto>> getAllSessionsByType(SessionByTypeRequest request) {
        logger.info("Received request to fetch all sessions");
        try {
            List<Session> sessions = sessionRepository.findByTypeAndDate(request.getSessionType(),
                    request.getSessionDate());
            logger.info("Successfully retrieved {} sessions from database", sessions.size());

            List<SessionDto> sessionDtos = sessions.stream()
                    .map(SessionDto::fromEntity)
                    .collect(Collectors.toList());

            logger.info("Successfully converted sessions to DTOs");
            return ResponseEntity.ok(sessionDtos);
        } catch (Exception e) {
            logger.error("Error fetching sessions: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/sessionDate")
    public ResponseEntity<List<SessionDto>> getSessionsByDate(@RequestParam LocalDate date) {
        logger.info("Fetching sessions for date: {}", date);
        List<Session> sessions = sessionRepository.findByDate(date);
        List<SessionDto> sessionDtos = sessions.stream()
                .map(SessionDto::fromEntity)
                .collect(Collectors.toList());
        logger.info("Found {} sessions for date {}", sessions.size(), date);
        logger.info("Session details for date {}: {}", date, sessionDtos);
        return ResponseEntity.ok(sessionDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        logger.info("Fetching session with id: {}", id);

        return sessionRepository.findById(id)
                .map(session -> {
                    logger.info("Found session with id: {}", id);
                    return ResponseEntity.ok(SessionDto.fromEntity(session));
                })
                .orElseGet(() -> {
                    logger.warn("Session not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/availability")
    public ResponseEntity<SessionAvailibilityResponse> getSessionAvailability(
            @RequestBody SessionAvailibilityRequest request) {

        if (isEmpty(request)) {
            logger.warn("No sessions found for the provided ids: {}", request.getSessionId());
            return ResponseEntity.notFound().build();
        }

        logger.info("Checking availability for sessions with ids: {}", request.getSessionId());
        SessionAvailibilityResponse session = sessionService.checkSessionAvailability(request);

        if (isEmpty(session)) {
            logger.error("No availability found for session with id: {}", request.getSessionId());
            throw new BusinessException(ErrorCode.SESSION_NOT_FOUND);
        }

        logger.info("Successfully retrieved availability for {} sessions", session.getSessionId());
        return ResponseEntity.ok(session);
    }

    @PostMapping
    public ResponseEntity<Session> createSession(@RequestBody Session session) {
        logger.info("Creating new session: {}", session);
        Session savedSession = sessionRepository.save(session);
        logger.info("Created session with id: {}", savedSession.getId());
        return ResponseEntity.ok(savedSession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Session> updateSession(@PathVariable Long id, @RequestBody Session session) {
        logger.info("Updating session with id: {}", id);
        return sessionRepository.findById(id)
                .map(existingSession -> {
                    logger.info("Found existing session with id: {}", id);
                    existingSession.setCoach(session.getCoach());
                    existingSession.setDate(session.getDate());
                    existingSession.setStartTime(session.getStartTime());
                    existingSession.setEndTime(session.getEndTime());
                    existingSession.setTennisField(session.getTennisField());
                    existingSession.setMaxParticipants(session.getMaxParticipants());
                    existingSession.setPricePerPerson(session.getPricePerPerson());
                    existingSession.setDescription(session.getDescription());
                    existingSession.setStatus(session.getStatus());
                    existingSession.setType(session.getType());
                    Session updatedSession = sessionRepository.save(existingSession);
                    logger.info("Updated session with id: {}", id);
                    return ResponseEntity.ok(updatedSession);
                })
                .orElseGet(() -> {
                    logger.warn("Session not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        logger.info("Attempting to delete session with id: {}", id);
        return sessionRepository.findById(id)
                .map(session -> {
                    sessionRepository.delete(session);
                    logger.info("Successfully deleted session with id: {}", id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> {
                    logger.warn("Session not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}