package com.booking.tennisbook.service.impl;

import com.booking.tennisbook.exception.BusinessException;
import com.booking.tennisbook.exception.ErrorCode;
import com.booking.tennisbook.model.Coach;
import com.booking.tennisbook.repository.CoachRepository;
import com.booking.tennisbook.service.CoachService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;

    public CoachServiceImpl(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    @Override
    @Transactional
    public Coach createCoach(Coach coach) {
        return coachRepository.save(coach);
    }

    @Override
    @Transactional
    public Coach updateCoach(Long id, Coach coach) {
        Coach existingCoach = coachRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        existingCoach.setName(coach.getName());
        existingCoach.setPhoneNumber(coach.getPhoneNumber());
        existingCoach.setBio(coach.getBio());

        return coachRepository.save(existingCoach);
    }

    @Override
    public Coach getCoach(Long id) {
        return coachRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }

    @Override
    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteCoach(Long id) {
        if (!coachRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        coachRepository.deleteById(id);
    }
} 