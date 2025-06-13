package com.booking.tennisbook.service;

import com.booking.tennisbook.model.Coach;
import java.util.List;

public interface CoachService {
    Coach createCoach(Coach coach);
    Coach updateCoach(Long id, Coach coach);
    Coach getCoach(Long id);
    List<Coach> getAllCoaches();
    void deleteCoach(Long id);
} 