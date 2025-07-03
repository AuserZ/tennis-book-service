package com.booking.tennisbook.repository;

import com.booking.tennisbook.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT s FROM Session s WHERE s.date = :date")
    List<Session> findByDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM Session s WHERE LOWER(s.type) = LOWER(:type) AND s.date = :date")
    List<Session> findByTypeAndDate(@Param("type") String type, @Param("date") LocalDate date);
    
} 