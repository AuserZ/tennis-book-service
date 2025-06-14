package com.booking.tennisbook.service.impl;

import com.booking.tennisbook.dto.session.SessionAvailibilityRequest;
import com.booking.tennisbook.dto.session.SessionAvailibilityResponse;
import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.repository.CoachRepository;
import com.booking.tennisbook.repository.SessionRepository;
import com.booking.tennisbook.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionServiceImpl implements SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);

    private final SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public SessionAvailibilityResponse checkSessionAvailability(SessionAvailibilityRequest request) {

        if(request.getSessionId() == null || request.getParticipants() <= 0) {
            logger.error("Invalid session availability request: {}", request);
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> {
                    logger.error("Session not found with ID: {}", request.getSessionId());
                    return new BusinessException(ErrorCode.SESSION_NOT_FOUND);
                });

        SessionAvailibilityResponse response = new SessionAvailibilityResponse();

        response.setSessionId(session.getId());
        response.setParticipants(session.getCurrentParticipants());
        response.setAvailable(session.getCurrentParticipants() + request.getParticipants() <= session.getMaxParticipants());

        return response;
    }

    @Override
    public Session updateSessionParticipants(Session session, int participants) {
        if (session.getCurrentParticipants() + participants > session.getMaxParticipants()) {
            logger.warn("Cannot update session participants. Current: {}, Requested: {}, Max: {}",
                    session.getCurrentParticipants(), participants, session.getMaxParticipants());
            throw new BusinessException(ErrorCode.SESSION_NOT_ENOUGH);
        }

        session.setCurrentParticipants(session.getCurrentParticipants() + participants);
        return sessionRepository.save(session);
    }
}
