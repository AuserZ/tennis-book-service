package com.booking.tennisbook.service;

import com.booking.tennisbook.dto.booking.BookingRequest;
import com.booking.tennisbook.model.Booking;
import com.booking.tennisbook.model.Session;
import com.booking.tennisbook.model.User;

public interface BookingService {
    Booking createBooking (BookingRequest bookingRequest);
}
