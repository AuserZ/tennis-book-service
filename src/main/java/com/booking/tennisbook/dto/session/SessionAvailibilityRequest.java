package com.booking.tennisbook.dto.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SessionAvailibilityRequest {
    private Long sessionId;
    private int participants;
}
