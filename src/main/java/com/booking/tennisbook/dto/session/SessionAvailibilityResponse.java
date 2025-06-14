package com.booking.tennisbook.dto.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SessionAvailibilityResponse {
    private Long sessionId;
    private int participants;
    private boolean available;


}
