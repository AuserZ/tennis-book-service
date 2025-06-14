package com.booking.tennisbook.service;

import com.booking.tennisbook.dto.session.SessionAvailibilityRequest;
import com.booking.tennisbook.dto.session.SessionAvailibilityResponse;
import com.booking.tennisbook.model.Session;

public interface SessionService {
    Session updateSessionParticipants(Session session, int participants);
    SessionAvailibilityResponse checkSessionAvailability(SessionAvailibilityRequest request);
}
