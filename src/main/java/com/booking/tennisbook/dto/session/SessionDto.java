package com.booking.tennisbook.dto.session;

import com.booking.tennisbook.dto.coach.CoachDto;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.model.TennisField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
    private Long id;
    private CoachDto coach;
    private TennisField tennisField;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal pricePerPerson;
    private String description;
    private String type;
    private Session.SessionStatus status;

    // Method to map Session entity to SessionDto
    public static SessionDto fromEntity(Session session) {
        return SessionDto.builder()
                .id(session.getId())
                .coach(CoachDto.builder()
                        .id(session.getCoach().getId())
                        .name(session.getCoach().getName())
                        .phoneNumber(session.getCoach().getPhoneNumber())
                        .build())
                .tennisField(session.getTennisField())
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .maxParticipants(session.getMaxParticipants())
                .currentParticipants(session.getCurrentParticipants())
                .pricePerPerson(session.getPricePerPerson())
                .description(session.getDescription())
                .type(session.getType())
                .status(session.getStatus())
                .build();
    }
} 