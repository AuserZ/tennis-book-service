package com.booking.tennisbook.repository;

import com.booking.tennisbook.model.DokuNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DokuNotificationLogRepository extends JpaRepository<DokuNotificationLog, Long> {
} 