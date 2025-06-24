package com.booking.tennisbook.dto.session;

import java.time.LocalDate;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SessionByTypeRequest {
    @NonNull
    private String sessionType;
    private LocalDate sessionDate;
}
