package com.booking.tennisbook.repository;

import com.booking.tennisbook.model.TennisField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TennisFieldRepository extends JpaRepository<TennisField, Long> {
} 