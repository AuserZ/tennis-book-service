package com.booking.tennisbook.repository;

import com.booking.tennisbook.model.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Long> {
} 